package com.google.code.lightssh.project.scheduler.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.StdScheduler;

import com.google.code.lightssh.common.util.StringUtil;
import com.google.code.lightssh.project.scheduler.entity.JobInterval;
import com.google.code.lightssh.project.scheduler.entity.TriggerWrap;

public class SchedulerManagerImpl implements SchedulerManager{
	
	private JobIntervalManager jobIntervalManager;
	
	private StdScheduler scheduler;

	public void setJobIntervalManager(JobIntervalManager jobIntervalManager) {
		this.jobIntervalManager = jobIntervalManager;
	}

	public void setQuartzScheduler(
			StdScheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	/**
	 * 获得系统Trigger
	 * @return
	 */
	private List<Trigger> listTriggers( ){
		List<Trigger> triggers = new ArrayList<Trigger>( );
		
		String groups[] = { Scheduler.DEFAULT_GROUP };
		try{
			groups = scheduler.getTriggerGroupNames();
		}catch( Exception e ){}
		
		try{
			for( String group:groups )
				for( String name:scheduler.getTriggerNames(group) ){
					triggers.add( scheduler.getTrigger( name, group ) );
				}
		}catch( Exception e ){}
		
		return triggers;
	}
	
	private Map<String,JobInterval> getJobIntervalMap( ){
		List<JobInterval> list = jobIntervalManager.listAvailable();
		if( list == null || list.isEmpty() )
			return null;
		
		Map<String,JobInterval> jiMap = new HashMap<String,JobInterval>();
		for( JobInterval ji:list )
			jiMap.put(ji.getTriggerName(), ji);
		
		return jiMap;
	}

	@Override
	public void initCronTrigger() {
		if( scheduler == null || jobIntervalManager == null )
			return;
		
		Map<String,JobInterval> jiMap = getJobIntervalMap();
		if( jiMap == null || jiMap.isEmpty() )
			return;
		
		List<Trigger> triggers = listTriggers( );
		if( triggers == null || triggers.isEmpty() )
			return;
		
		//针对每个Trigger 设置Cron表示达
		for( Trigger trigger:triggers ){
			JobInterval jobInterval = jiMap.get( trigger.getName() );
			if( trigger instanceof CronTrigger && jobInterval != null ){
				try{
					if( jobInterval.isEnabled() ){ //重设置trigger
						((CronTrigger)trigger).setCronExpression( jobInterval.getCronExpression() );
						scheduler.rescheduleJob(trigger.getName(), trigger.getGroup(), trigger);
					}else{//停用
						scheduler.pauseTrigger(trigger.getName(), trigger.getGroup());
					}
				}catch( Exception e ){
					//e.printStackTrace();
				}
			}
		}//end for
	}

	@Override
	public void changeCronTrigger(String triggerName, String cronExpression) {
		if( triggerName == null || cronExpression == null )
			return;
		
		JobInterval jobInterval = jobIntervalManager.get( triggerName );
		if( jobInterval == null )
			return;
		jobInterval.setCronExpression(cronExpression);
		jobIntervalManager.save(jobInterval);
		
		List<Trigger> triggers = listTriggers( );
		if( triggers == null || triggers.isEmpty() )
			return;
		
		for( Trigger trigger:triggers ){
			if( triggerName.equals( trigger.getName() ) && trigger instanceof CronTrigger ){
				try{
					((CronTrigger)trigger).setCronExpression( jobInterval.getCronExpression() );
					scheduler.rescheduleJob(trigger.getName(), trigger.getGroup(), trigger);
				}catch( Exception e ){
					//e.printStackTrace();
				}
				break;
			}
		}//end for
	}

	@Override
	public List<TriggerWrap> listAllTrigger() {
		List<TriggerWrap> result = null;
		List<Trigger> list = listTriggers();
		
		if( list != null && !list.isEmpty() ){
			result = new ArrayList<TriggerWrap>();
			
			Map<String,JobInterval> jiMap = getJobIntervalMap();
			
			for(Trigger trigger:list ){
				TriggerWrap wrap = new TriggerWrap( (Trigger)trigger.clone() );
				wrap.setPause((jiMap!=null && !jiMap.isEmpty() 
					&& (jiMap.get(trigger.getName())!=null)
					&& (!jiMap.get(trigger.getName()).isEnabled()))?true:false);
				result.add(wrap);
			}
		}
		
		return result;
	}
	
	public void toggleTrigger(String triggerName ){
		if( StringUtil.clean(triggerName)==null )
			return;
		
		String cronExpression = null;
		JobInterval jobInterval = jobIntervalManager.get( triggerName );
		if( jobInterval != null ){
			jobInterval.setEnabled( Boolean.valueOf( !jobInterval.isEnabled() ) );
			jobIntervalManager.save(jobInterval);
			cronExpression = StringUtil.clean(jobInterval.getCronExpression());
		}
		
		List<Trigger> triggers = listTriggers( );
		if( triggers == null || triggers.isEmpty() )
			return;
		
		for( Trigger trigger:triggers ){
			if( triggerName.equals( trigger.getName() ) ){
				try{
					int state = scheduler.getTriggerState(trigger.getName(), trigger.getGroup());
					if( Trigger.STATE_PAUSED == state ){
						if( trigger instanceof CronTrigger && cronExpression != null){
							((CronTrigger)trigger).setCronExpression(cronExpression);
							scheduler.rescheduleJob(trigger.getName(), trigger.getGroup(), trigger);
						}
						scheduler.resumeTrigger(trigger.getName(), trigger.getGroup());
					}else
						scheduler.pauseTrigger(trigger.getName(), trigger.getGroup());
				}catch( Exception e ){
					//e.printStackTrace();
				}
				break;
			}
		}//end for
	}
	
}