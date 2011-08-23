package com.google.code.lightssh.project.security.dao;

import java.util.Collection;
import java.util.List;

import com.google.code.lightssh.common.dao.hibernate.HibernateDao;
import com.google.code.lightssh.project.security.entity.Navigation;
import com.google.code.lightssh.project.security.entity.Permission;

/**
 * Navigation Dao Hibernate
 * @author YangXiaojin
 *
 */
public class NavigationDaoHibernate extends HibernateDao<Navigation> implements NavigationDao{

	@SuppressWarnings("unchecked")
	@Override
	public List<Navigation> listTop() {
		String hql = " SELECT n FROM Navigation AS n WHERE n.parent IS NULL ";
		
		return super.getHibernateTemplate().find(hql);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Permission> listPermission(Collection<Navigation> colls) {
		if( colls == null || colls.isEmpty() )
			return null;
		
		StringBuffer hql = new StringBuffer( 
				" SELECT n.permission FROM Navigation AS n WHERE n.id in ( " );
		boolean isFirst = true;
		for( Navigation item:colls ){
			if( item == null || item.getId() == null )
				continue;
			hql.append( (isFirst?"":",") + item.getId() );
			isFirst = false;
		}
		hql.append(" ) ");
		
		return super.getHibernateTemplate().find(hql.toString());
	}

}