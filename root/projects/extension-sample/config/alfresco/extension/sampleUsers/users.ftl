<!-- Template for generating Users -->
<?xml version="1.0" encoding="UTF-8"?>
<view:view xmlns:view="http://www.alfresco.org/view/repository/1.0" xmlns:alf="http://www.alfresco.org" xmlns:d="http://www.alfresco.org/model/dictionary/1.0" xmlns:sys="http://www.alfresco.org/model/system/1.0" xmlns:act="http://www.alfresco.org/model/action/1.0" xmlns:rule="http://www.alfresco.org/model/rule/1.0" xmlns:fm="http://www.alfresco.org/model/forum/1.0" xmlns:app="http://www.alfresco.org/model/application/1.0" xmlns:usr="http://www.alfresco.org/model/user/1.0" xmlns:ver="http://www.alfresco.org/model/versionstore/1.0" xmlns:cm="http://www.alfresco.org/model/content/1.0" xmlns="">
<#assign USER_COUNT=6000>
<#list 1..USER_COUNT as i>
  <usr:user view:childName="usr:user">
    <view:properties>
      <usr:username>${i?string("0000")}</usr:username>
      <usr:password>e0fba38268d0ec66ef1cb452d5885e53</usr:password>  <!-- default password of 'abc' -->
      <usr:accountExpires>false</usr:accountExpires>
      <usr:credentialsExpire>false</usr:credentialsExpire>
      <usr:accountLocked>false</usr:accountLocked>
      <usr:enabled>true</usr:enabled>
    </view:properties>
  </usr:user>
</#list>
</view:view>