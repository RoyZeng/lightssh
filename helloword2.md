# 开发步骤 #

  1. ENTITY
  1. DAO
  1. Service
  1. ACTION
  1. VIEW

###### 1. ENTITY|创建实体，并作JPA映射 ######
```

@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class Party{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * CODE
	 */
	@Id
	@Column( name="ID" )
	private String id;
	
	/**
	 * 名称
	 */
	@Column( name="NAME",unique=false,length=100 )
	private String name;
	
	/**
	 * enabled
	 */
	@Column( name="ENABLED" )
	protected Boolean enabled;
	
	/**
	 * 描述
	 */
	@Column( name="DESCRIPTION",length=200 )
	protected String description;

	//省略 getters and setters 
}

```

###### 2. DAO|创建dao接口，并作实现 ######
```

import com.google.code.lightssh.common.dao.Dao;
import com.google.code.lightssh.common.model.page.ListPage;
import com.google.code.lightssh.project.party.entity.Party;

/**
 * 
 * @author aspen_61682@yahoo.com.cn
 *
 */
public interface PartyDao extends Dao<Party>{
	
	/**
	 * 查询具体子类
	 */
	public ListPage<Party> list(Class<?> clazz,ListPage<Party> page,Party t );
	
	/**
	 * 查询具体子类
	 */
	public Party read( Class<?> clazz,Party party );

}

```

PartyDao hibernate 实现
```

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.google.code.lightssh.common.dao.hibernate.HibernateAnnotationDao;
import com.google.code.lightssh.common.model.page.ListPage;
import com.google.code.lightssh.common.util.ReflectionUtil;
import com.google.code.lightssh.project.party.entity.Organization;
import com.google.code.lightssh.project.party.entity.Party;
import com.google.code.lightssh.project.party.entity.Person;

@Repository("partyDao")
public class PartyDaoHibernate extends HibernateAnnotationDao<Party> implements PartyDao{
	
	public ListPage<Party> list(ListPage<Party> page,Party t ){
		return this.list( super.entityClass, page, t );
	}
	
	public ListPage<Party> list(Class<?> clazz,ListPage<Party> page,Party t ){
		//do somethings...
	}
	
	public ListPage<Party> list(Class<?> clazz,ListPage<Party> page, Party t,Collection<String> properties ){
		//do somethings...
	}
	
	public ListPage<Party> list(ListPage<Party> page, Party t,Collection<String> properties ){
		Class<?> clazz = Organization.class;
		if( t instanceof Person )
			clazz = Person.class;
		return super.list(clazz, page, t, properties);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Party read(Class clazz, Party party) {
		if( party == null )
			return null;
		return this.getHibernateTemplate().get(clazz,party.getIdentity());
	}

}


```

###### 3. SERVICE|创建业务接口，并作实现 ######

```

import com.google.code.lightssh.common.model.page.ListPage;
import com.google.code.lightssh.common.service.BaseManager;
import com.google.code.lightssh.project.log.entity.Access;
import com.google.code.lightssh.project.party.entity.Organization;
import com.google.code.lightssh.project.party.entity.Party;
import com.google.code.lightssh.project.party.entity.Person;
import com.google.code.lightssh.project.party.entity.PartyRole.RoleType;

/**
 * 
 * @author YangXiaojin
 *
 */
public interface PartyManager extends BaseManager<Party>{
	
	/**
	 * 带条件的分页查询
	 */
	public ListPage<Party> listPerson(ListPage<Party> page,Party party );
	
	/**
	 * 带条件的分页查询
	 */
	public ListPage<Party> listOrganization(ListPage<Party> page,Party party );
	
	/**
	 * 查询 Organization
	 */
	public Organization getOrganization( Party party );

	//more here...
}

```

```


@Component("partyManager")
public class PartyManagerImpl extends BaseManagerImpl<Party> implements PartyManager{
	
	@Resource(name="sequenceManager")
	private SequenceManager sequenceManager;
	
	
	@Resource(name="partyDao")
	public void setDao( PartyDao dao ){
		super.dao = dao;
	}
	
	public PartyDao getDao(){
		return (PartyDao)super.dao;
	}
	
	public void remove(Party t, Access access) {
		Class<?> clazz = Person.class;
		if( t instanceof Organization )
			clazz = Organization.class;
		
		Party party = getDao().read(clazz,t);
		if( party != null ){
			dao.delete(party);
			
			if( access != null && isDoLog(party))
				accessManager.log(access, party, null);
		}
		
	}
	
	public void remove(Organization t, Access access) {
		//...
	}

	@Override
	public void save(Party party, Access access) {
		if( party == null )
			throw new ApplicationException("数据为空，不能进行保存！");
		
		Party original = null;
		boolean inserted = StringUtil.clean( party.getIdentity() ) == null;
		
		//...
	}

	//more here ...
}


```

###### 4. ACTION|创建action ######

```

@Component( "partyAction" )
@Scope("prototype")
public class PartyAction extends CrudAction<Party>{

	private static final long serialVersionUID = 669140342947692813L;
	
	private static final String FAMILY = "family";
	private static final String CONTACT = "contact";
	
	private Party party;
	
	private boolean unique;
	
	@Resource( name="partyManager" )
	public void setPartyManager( PartyManager manager ){
		super.manager = manager;
	}
	
	public PartyManager getManager( ){
		return (PartyManager)this.manager;
	}

	@JSON(name="unique")
	public boolean isUnique() {
		return unique;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	public Party getParty() {
		this.party = super.model;
		return party;
	}

	public void setParty(Party party) {
		this.party = party;
		super.model = this.party;
	}
	
	public String edit( ){
		//...
         
	}
	
    /**
    * list
    * @return
    */
    public String list( ){       
        //...
         
    }
    
    /**
     * save
     * @return
     */
     public String save( ){    	
         //...
         
     }
     
     /**
      * delete
      * @return
      */
      public String delete( ){
          //...
         
          return SUCCESS;
     }
      
  	public String unique( ){
		this.unique = this.getManager().isUniqneName( party );
		return SUCCESS;
	}

}

```