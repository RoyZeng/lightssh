# 关于DAO #

DAO 模式对任何企业 Java 开发人员来说都应该很熟悉。但是模式的实现各不相同，所以我们来澄清一下本文提供的 DAO 实现背后的假设：

  * 系统中的所有数据库访问都通过 DAO 进行以实现封装。
  * 每个 DAO 实例负责一个主要域对象或实体。如果域对象具有独立生命周期，它应具有自己的 DAO。
  * DAO 负责域对象的创建、读取（按主键）、更新和删除（creations, reads, updates, and deletions，CRUD）。
  * DAO 可允许基于除主键之外的标准进行查询。我将之称为查找器方法 或查找器。查找器的返回值通常是 DAO 负责的域对象集合。
  * DAO 不负责处理事务、会话或连接。这些不由 DAO 处理是为了实现灵活性。


# 泛型DAO接口 #

```
package com.google.code.lightssh.common.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.google.code.lightssh.common.entity.Persistence;
import com.google.code.lightssh.common.model.page.ListPage;


/** 
 * data access object ,CURD
 */
public interface Dao<T extends Persistence<?>> {
	
	/**
	 * create
	 */
	public void create( T t );
	
	/**
	 * create all
	 */
	public void create( Collection<T> entities );
	
	/**
	 * update
	 */
	public void update( T t );
	
	/**
	 * update
	 */
	public int update(String idName,Object idValue
			,String property,Object originalValue,Object newValue );
	
	/**
	 * update all 
	 */
	public void update( Collection<T> entities );
	
	/**
	 * read
	 */
	public T read( T t);
	
	/**
	 * read
	 */
	public T read(Serializable identity);
			
	/**
	 * delete
	 */
	public void delete( T t );
	
	/**
	 * delete
	 */
	public void delete( Serializable identity );
	
	/**
	 * delete all
	 */
	public void delete( Collection<T> entities );	
	
	/**
	 * delete all
	 */
	public void deleteAll( );	
	
	/**
	 * list all
	 */
	public List<T> listAll( );
	
	/**
	 * 分页查询
	 */
	public ListPage<T> list( ListPage<T> page );
	
	/**
	 * 带条件的分页查询
	 */
	public ListPage<T> list(ListPage<T> page,T t );
	
	/**
	 * 匹配指定属性的分页查询
	 */
	public ListPage<T> list(ListPage<T> page,T t,Collection<String> properties );
	
	/**
	 * 带条件的分页查询
	 */
	public ListPage<T> list( ListPage<T> page,String select,Collection<Term> terms );
	
	/**
	 * 带条件的分页查询
	 */
	public ListPage<T> list( ListPage<T> page,Collection<Term> terms );
	
	/**
	 * 带条件的分页查询
	 */
	public ListPage<T> list( ListPage<T> page,String select,SearchCondition sc );
	
	/**
	 * 带条件的分页查询
	 */
	public ListPage<T> list( ListPage<T> page,SearchCondition sc );
}

```

# 配置DAO #

如果你所需要的DAO实现比较简单，接口中提及的方式能很好满足，那么你不需要编写任何DAO实现的Java代码，只需要如下一段Spring的配置，你有拥有了类型安全的DAO实现。配置JPA实现的DAO,示例如下：
```
<bean id="permissionDao" class="com.google.code.lightssh.common.dao.jpa.JpaDao">
	<constructor-arg>
    		<value>com.google.code.lightssh.project.security.entity.Permission</value>
    	</constructor-arg>    
        <property name="entityManagerFactory" ref="entityManagerFactory"/>
</bean>

```


# 参考资料 #
  1. http://www.ibm.com/developerworks/cn/java/j-genericdao.html