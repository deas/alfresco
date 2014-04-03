/* -*- Mode: Java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */
/* vim: set shiftwidth=2 tabstop=2 autoindent cindent expandtab: */
/* Copyright 2012 Mozilla Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
'use strict';if(typeof PDFJS==='undefined'){(typeof window!=='undefined'?window:this).PDFJS={};}
(function checkTypedArrayCompatibility(){if(typeof Uint8Array!=='undefined'){if(typeof Uint8Array.prototype.subarray==='undefined'){Uint8Array.prototype.subarray=function subarray(start,end){return new Uint8Array(this.slice(start,end));};Float32Array.prototype.subarray=function subarray(start,end){return new Float32Array(this.slice(start,end));};}
if(typeof Float64Array==='undefined')
window.Float64Array=Float32Array;return;}
function subarray(start,end){return new TypedArray(this.slice(start,end));}
function setArrayOffset(array,offset){if(arguments.length<2)
offset=0;for(var i=0,n=array.length;i<n;++i,++offset)
this[offset]=array[i]&0xFF;}
function TypedArray(arg1){var result;if(typeof arg1==='number'){result=[];for(var i=0;i<arg1;++i)
result[i]=0;}else if('slice'in arg1){result=arg1.slice(0);}else{result=[];for(var i=0,n=arg1.length;i<n;++i){result[i]=arg1[i];}}
result.subarray=subarray;result.buffer=result;result.byteLength=result.length;result.set=setArrayOffset;if(typeof arg1==='object'&&arg1.buffer)
result.buffer=arg1.buffer;return result;}
window.Uint8Array=TypedArray;window.Uint32Array=TypedArray;window.Int32Array=TypedArray;window.Uint16Array=TypedArray;window.Float32Array=TypedArray;window.Float64Array=TypedArray;})();(function normalizeURLObject(){if(!window.URL){window.URL=window.webkitURL;}})();(function checkObjectCreateCompatibility(){if(typeof Object.create!=='undefined')
return;Object.create=function objectCreate(proto){function Constructor(){}
Constructor.prototype=proto;return new Constructor();};})();(function checkObjectDefinePropertyCompatibility(){if(typeof Object.defineProperty!=='undefined'){var definePropertyPossible=true;try{Object.defineProperty(new Image(),'id',{value:'test'});var Test=function Test(){};Test.prototype={get id(){}};Object.defineProperty(new Test(),'id',{value:'',configurable:true,enumerable:true,writable:false});}catch(e){definePropertyPossible=false;}
if(definePropertyPossible)return;}
Object.defineProperty=function objectDefineProperty(obj,name,def){delete obj[name];if('get'in def)
obj.__defineGetter__(name,def['get']);if('set'in def)
obj.__defineSetter__(name,def['set']);if('value'in def){obj.__defineSetter__(name,function objectDefinePropertySetter(value){this.__defineGetter__(name,function objectDefinePropertyGetter(){return value;});return value;});obj[name]=def.value;}};})();(function checkObjectKeysCompatibility(){if(typeof Object.keys!=='undefined')
return;Object.keys=function objectKeys(obj){var result=[];for(var i in obj){if(obj.hasOwnProperty(i))
result.push(i);}
return result;};})();(function checkFileReaderReadAsArrayBuffer(){if(typeof FileReader==='undefined')
return;var frPrototype=FileReader.prototype;if('readAsArrayBuffer'in frPrototype)
return;Object.defineProperty(frPrototype,'readAsArrayBuffer',{value:function fileReaderReadAsArrayBuffer(blob){var fileReader=new FileReader();var originalReader=this;fileReader.onload=function fileReaderOnload(evt){var data=evt.target.result;var buffer=new ArrayBuffer(data.length);var uint8Array=new Uint8Array(buffer);for(var i=0,ii=data.length;i<ii;i++)
uint8Array[i]=data.charCodeAt(i);Object.defineProperty(originalReader,'result',{value:buffer,enumerable:true,writable:false,configurable:true});var event=document.createEvent('HTMLEvents');event.initEvent('load',false,false);originalReader.dispatchEvent(event);};fileReader.readAsBinaryString(blob);}});})();(function checkXMLHttpRequestResponseCompatibility(){var xhrPrototype=XMLHttpRequest.prototype;if(!('overrideMimeType'in xhrPrototype)){Object.defineProperty(xhrPrototype,'overrideMimeType',{value:function xmlHttpRequestOverrideMimeType(mimeType){}});}
if('response'in xhrPrototype||'mozResponseArrayBuffer'in xhrPrototype||'mozResponse'in xhrPrototype||'responseArrayBuffer'in xhrPrototype)
return;if(typeof VBArray!=='undefined'){Object.defineProperty(xhrPrototype,'response',{get:function xmlHttpRequestResponseGet(){return new Uint8Array(new VBArray(this.responseBody).toArray());}});return;}
function responseTypeSetter(){this.overrideMimeType('text/plain; charset=x-user-defined');}
if(typeof xhrPrototype.overrideMimeType==='function'){Object.defineProperty(xhrPrototype,'responseType',{set:responseTypeSetter});}
function responseGetter(){var text=this.responseText;var i,n=text.length;var result=new Uint8Array(n);for(i=0;i<n;++i)
result[i]=text.charCodeAt(i)&0xFF;return result;}
Object.defineProperty(xhrPrototype,'response',{get:responseGetter});})();(function checkWindowBtoaCompatibility(){if('btoa'in window)
return;var digits='ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';window.btoa=function windowBtoa(chars){var buffer='';var i,n;for(i=0,n=chars.length;i<n;i+=3){var b1=chars.charCodeAt(i)&0xFF;var b2=chars.charCodeAt(i+1)&0xFF;var b3=chars.charCodeAt(i+2)&0xFF;var d1=b1>>2,d2=((b1&3)<<4)|(b2>>4);var d3=i+1<n?((b2&0xF)<<2)|(b3>>6):64;var d4=i+2<n?(b3&0x3F):64;buffer+=(digits.charAt(d1)+digits.charAt(d2)+
digits.charAt(d3)+digits.charAt(d4));}
return buffer;};})();(function checkWindowAtobCompatibility(){if('atob'in window)
return;var digits='ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';window.atob=function(input){input=input.replace(/=+$/,'');if(input.length%4==1)throw new Error('bad atob input');for(var bc=0,bs,buffer,idx=0,output='';buffer=input.charAt(idx++);~buffer&&(bs=bc%4?bs*64+buffer:buffer,bc++%4)?output+=String.fromCharCode(255&bs>>(-2*bc&6)):0){buffer=digits.indexOf(buffer);}
return output;};})();(function checkFunctionPrototypeBindCompatibility(){if(typeof Function.prototype.bind!=='undefined')
return;Function.prototype.bind=function functionPrototypeBind(obj){var fn=this,headArgs=Array.prototype.slice.call(arguments,1);var bound=function functionPrototypeBindBound(){var args=headArgs.concat(Array.prototype.slice.call(arguments));return fn.apply(obj,args);};return bound;};})();(function checkDatasetProperty(){var div=document.createElement('div');if('dataset'in div)
return;Object.defineProperty(HTMLElement.prototype,'dataset',{get:function(){if(this._dataset)
return this._dataset;var dataset={};for(var j=0,jj=this.attributes.length;j<jj;j++){var attribute=this.attributes[j];if(attribute.name.substring(0,5)!='data-')
continue;var key=attribute.name.substring(5).replace(/\-([a-z])/g,function(all,ch){return ch.toUpperCase();});dataset[key]=attribute.value;}
Object.defineProperty(this,'_dataset',{value:dataset,writable:false,enumerable:false});return dataset;},enumerable:true});})();(function checkClassListProperty(){var div=document.createElement('div');if('classList'in div)
return;function changeList(element,itemName,add,remove){var s=element.className||'';var list=s.split(/\s+/g);if(list[0]==='')list.shift();var index=list.indexOf(itemName);if(index<0&&add)
list.push(itemName);if(index>=0&&remove)
list.splice(index,1);element.className=list.join(' ');return(index>=0);}
var classListPrototype={add:function(name){changeList(this.element,name,true,false);},contains:function(name){return changeList(this.element,name,false,false);},remove:function(name){changeList(this.element,name,false,true);},toggle:function(name){changeList(this.element,name,true,true);}};Object.defineProperty(HTMLElement.prototype,'classList',{get:function(){if(this._classList)
return this._classList;var classList=Object.create(classListPrototype,{element:{value:this,writable:false,enumerable:true}});Object.defineProperty(this,'_classList',{value:classList,writable:false,enumerable:false});return classList;},enumerable:true});})();(function checkConsoleCompatibility(){if(!('console'in window)){window.console={log:function(){},error:function(){},warn:function(){}};}else if(!('bind'in console.log)){console.log=(function(fn){return function(msg){return fn(msg);};})(console.log);console.error=(function(fn){return function(msg){return fn(msg);};})(console.error);console.warn=(function(fn){return function(msg){return fn(msg);};})(console.warn);}})();(function checkOnClickCompatibility(){function ignoreIfTargetDisabled(event){if(isDisabled(event.target)){event.stopPropagation();}}
function isDisabled(node){return node.disabled||(node.parentNode&&isDisabled(node.parentNode));}
if(navigator.userAgent.indexOf('Opera')!=-1){document.addEventListener('click',ignoreIfTargetDisabled,true);}})();(function checkOnBlobSupport(){if(navigator.userAgent.indexOf('Trident')>=0){PDFJS.disableCreateObjectURL=true;}})();(function checkNavigatorLanguage(){if('language'in navigator&&/^[a-z]+(-[A-Z]+)?$/.test(navigator.language)){return;}
function formatLocale(locale){var split=locale.split(/[-_]/);split[0]=split[0].toLowerCase();if(split.length>1){split[1]=split[1].toUpperCase();}
return split.join('-');}
var language=navigator.language||navigator.userLanguage||'en-US';PDFJS.locale=formatLocale(language);})();(function checkRangeRequests(){var isSafari=Object.prototype.toString.call(window.HTMLElement).indexOf('Constructor')>0;var regex=/Android\s[0-2][^\d]/;var isOldAndroid=regex.test(navigator.userAgent);if(isSafari||isOldAndroid){PDFJS.disableRange=true;}})();(function checkHistoryManipulation(){if(!window.history.pushState){PDFJS.disableHistory=true;}})();
