<!-- Template for generating Home Folders -->
<?xml version="1.0" encoding="UTF-8"?>
<view:view xmlns:view="http://www.alfresco.org/view/repository/1.0">
  <cm:folder xmlns:alf="http://www.alfresco.org" xmlns:d="http://www.alfresco.org/model/dictionary/1.0" xmlns:sys="http://www.alfresco.org/model/system/1.0" xmlns:act="http://www.alfresco.org/model/action/1.0" xmlns:rule="http://www.alfresco.org/model/rule/1.0" xmlns:fm="http://www.alfresco.org/model/forum/1.0" xmlns:app="http://www.alfresco.org/model/application/1.0" xmlns:usr="http://www.alfresco.org/model/user/1.0" xmlns:ver="http://www.alfresco.org/model/versionstore/1.0" xmlns:cm="http://www.alfresco.org/model/content/1.0" xmlns="" view:childName="cm:home_folders">
    <view:aspects>
      <app:uifacets></app:uifacets>
    </view:aspects>
    <view:properties>
      <app:icon>space-icon-default</app:icon>
      <cm:description>Location of all Home Folders</cm:description>
      <cm:title>Home Folders</cm:title>
      <cm:name>Home Folders</cm:name>
    </view:properties>
    <view:associations>
      <cm:contains>
<#assign USER_COUNT=6000>
<#list 1..USER_COUNT as i>
        <cm:folder view:childName="cm:hf_${i?string("0000")}">
          <view:aspects>
            <app:uifacets></app:uifacets>
          </view:aspects>
          <view:properties>
            <app:icon>space-icon-default</app:icon>
            <cm:description>${i?string("0000")} Home Folder</cm:description>
            <cm:title>${i?string("0000")} Home Folder</cm:title>
            <cm:name>${i?string("0000")} Home Folder</cm:name>
          </view:properties>
        </cm:folder>
</#list>
      </cm:contains>
    </view:associations>
  </cm:folder>
</view:view>