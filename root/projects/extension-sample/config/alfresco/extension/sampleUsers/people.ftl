<!-- Template for generating People -->
<?xml version="1.0" encoding="UTF-8"?>
<view:view xmlns:view="http://www.alfresco.org/view/repository/1.0" xmlns:alf="http://www.alfresco.org" xmlns:d="http://www.alfresco.org/model/dictionary/1.0" xmlns:sys="http://www.alfresco.org/model/system/1.0" xmlns:act="http://www.alfresco.org/model/action/1.0" xmlns:rule="http://www.alfresco.org/model/rule/1.0" xmlns:fm="http://www.alfresco.org/model/forum/1.0" xmlns:app="http://www.alfresco.org/model/application/1.0" xmlns:usr="http://www.alfresco.org/model/user/1.0" xmlns:ver="http://www.alfresco.org/model/versionstore/1.0" xmlns:cm="http://www.alfresco.org/model/content/1.0" xmlns="">
<#assign USER_COUNT=6000>
<#list 1..USER_COUNT as i>
  <cm:person view:childName="cm:person">
    <view:acl>
      <view:ace view:access="ALLOWED">
        <view:authority>${i?string("0000")}</view:authority>
        <view:permission>All</view:permission>
      </view:ace>
    </view:acl>
    <view:properties>
      <cm:firstName>${i?string("0000")} First Name</cm:firstName>
      <cm:lastName>${i?string("0000")} Last Name</cm:lastName>
      <cm:email>${i?string("0000")} Email address</cm:email>
      <cm:userName>${i?string("0000")}</cm:userName>
      <cm:homeFolder>../../../app:company_home/cm:home_folders/cm:hf_${i?string("0000")}</cm:homeFolder>
      <cm:organizationId></cm:organizationId>
    </view:properties>
  </cm:person>
</#list>
</view:view>