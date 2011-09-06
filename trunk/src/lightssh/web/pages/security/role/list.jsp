<%@ page language="java" contentType="text/html;charset=utf-8"%>
<%@ include file="/pages/common/taglibs.jsp" %>

<html>
	<head>
		<meta name="decorator" content="background"/>
		<title>角色列表</title>
		
		<script type="text/javascript">
			function doRemove( id,name ){
				var url = '<s:url value="/security/role/remove.do?role.id="/>' + id ;
				if( confirm('确认删除角色[' + name + ']'))
					location.href=url;
			}
		</script>
	</head>
	
	<body>
		<ul class="path">
			<li>系统管理</li>
			<li>角色管理</li>
			<li>角色列表</li>
		</ul>
		
		<%@ include file="/pages/common/messages.jsp" %>
		
		<s:form name="list" namespace="/system/role" method="post">
			<table class="profile">
				<tbody>
					<tr>
						<th><label for="name">名称</label></th>
						<td><s:textfield id="name" name="role.name" size="40" maxlength="100"/></td>
						<td colspan="2"><input type="submit" class="action search" value="查询"/></td>
					</tr>
				</tbody>
			</table>
		</s:form>
	
		<table class="list">
			<colgroup>
				<col class="element" width="50px"/>
				<col class="element" width="200px"/>
				<col class="element" width="100px"/>
				<col class="element" />
				<col class="element" width="100px"/>
			</colgroup>
			<thead>
				<tr>
					<th>序号</th>
					<th>名称</th>
					<th>创建日期</th>
					<th>描述</th>
					<th>操作</th>
				</tr>
			</thead>
			
			<s:iterator value="page.list" status="loop">
			<tr class="<s:property value="#loop.odd?\"odd\":\"even\""/>">
				<td><s:property value="#loop.index+1"/></td>
				<td><a href="<s:url value="/security/role/edit.do?role.id=%{id}"/>"><s:property value="%{name}"/></a></td>
				<td><s:property value="%{createDate}"/></td>
				<td><s:property value="%{description}"/></td>
				<td>
					<a href="<s:url value="/security/role/permission.do?role.id=%{id}"/>">权限</a>
					<a href="#" onclick="javascript:doRemove('<s:property value="%{id}"/>','<s:property value="%{name}"/>')">删除</a>
				</td>
			</tr>
			</s:iterator>
			</table>
			
			<s:set name="pagination" value="%{page}"/>
			<jsp:include page="/pages/common/pagination.jsp"/>
	</body>
</html>