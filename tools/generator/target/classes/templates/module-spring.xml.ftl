
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                           http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/context
                           http://www.springframework.org/schema/context/spring-context.xsd">

    <!-- Component scanning for the ${entity.entity} module -->
    <context:component-scan base-package="${entity.packageName}"/>

    <!-- Entity definition for collection management -->
    <bean id="${entity.entity?uncap_first}Definition" class="com.easybase.core.collection.EntityDefinition">
        <property name="name" value="${entity.entity}"/>
        <property name="tableName" value="${entity.table}"/>
        <property name="fields">
            <list>
                <#list entity.fields as field>
                <bean class="com.easybase.core.collection.FieldDefinition">
                    <property name="name" value="${field.name}"/>
                    <property name="type" value="${field.type}"/>
                    <property name="primaryKey" value="${field.primaryKey?string('true', 'false')}"/>
                    <property name="nullable" value="${field.nullable?string('true', 'false')}"/>
                    <#if field.search??>
                    <property name="searchMapping">
                        <map>
                            <entry key="type" value="${field.search.type}"/>
                            <#if field.search.analyzer??>
                            <entry key="analyzer" value="${field.search.analyzer}"/>
                            </#if>
                        </map>
                    </property>
                    </#if>
                </bean>
                </#list>
            </list>
        </property>
    </bean>

    <!-- Register the entity with the collection service -->
    <bean class="com.easybase.core.collection.EntityRegistrar">
        <property name="collectionService" ref="collectionService"/>
        <property name="entityDefinition" ref="${entity.entity?uncap_first}Definition"/>
    </bean>
</beans>