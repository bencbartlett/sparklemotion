#!/usr/bin/env node
"use strict";function f(t,n){return t.slice(0,n.length)==n}var e=/[\\^$.*+?()[\]{}|]/g,r=RegExp(e.source);function o(t,n){null!==n&&t.push(n)}function u(t){console.log(t)}var t=function(){function t(t,n){this.t=n,this.n=t||1e6+Math.floor(Math.random()*(9999e6+1))}return t.prototype.e=function(t,e){e.flowId=this.n,e.timestamp=(new Date).toISOString().slice(0,-1);var n=Object.keys(e).map(function(t){return t+"='"+((n=e[t])?n.toString().replace(/\|/g,"||").replace(/\n/g,"|n").replace(/\r/g,"|r").replace(/\[/g,"|[").replace(/\]/g,"|]").replace(/\u0085/g,"|x").replace(/\u2028/g,"|l").replace(/\u2029/g,"|p").replace(/'/g,"|'"):"")+"'";var n}).join(" ");this.t("##teamcity["+t+" "+n+"]")},t}(),n=require("kotlin-test"),s="skip",l="reportAsIgnoredTest",c="reportAllInnerTestsAsIgnored";function a(t,n){return(n=n||{}).name=t,n}var i=function(){function t(t){this.r=t}return t.prototype.i=function(){var t=this.r;for(var n in u(t.u+" v"+t.s+" - "+t.r),u(),u("Usage: "+t.u+" "+t.o),u(),t.f){var e=t.f[n];u("  "+e.keys.join(", "));var r="    ";if(u(""+r+e.l),e.values&&e.c){u(r+"Possible values:");for(var i=0;i<e.values.length;i++){u(r+' - "'+e.values[i]+'": '+e.c[i])}}e.a&&u(r+"By default: "+e.a),u("")}},t.prototype.h=function(t){u(t),u(),this.i(),process.v(1)},t.prototype.parse=function(t){var n=this.r,e={d:[]};for(var r in n.f)n.f[r].g||(e[r]=[]);t:for(;0!=t.length;){var i=t.shift();if(f(i,"--")){for(var u in n.f){var s=n.f[u];if(-1!=s.keys.indexOf(i)){0==t.length&&this.h("Missed value after option "+i);var o=t.shift();s.values&&-1==s.values.indexOf(o)&&this.h("Unsupported value for option "+i),s.g?e[u]=o:e[u].push(o);continue t}}this.h("Unknown option: "+i)}else e.d.push(i)}return 0==e.d.length&&this.h("At least one "+n.p+" should be provided"),e},t}();function h(t){if(null==t)return null;if(0==(t=(t=t.replace(/^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g,"")).replace(/\*+/,"*")).length)return null;if("*"==t)return v;if(-1==t.indexOf("*"))return new g(t);if(f(t,"*"))return new p(t);var n=t.split("*",2),e=n[0],r=n[1];return new d(e,r?new p(t):null)}var v=new(function(){function t(){}return t.prototype.y=function(t){return!0},t.prototype.S=function(t){return!0},t}()),d=function(){function t(t,n){this.w=t,this.filter=n}return t.prototype.x=function(t){return f(t+".",this.w)},t.prototype.y=function(t){return this.x(t)},t.prototype.m=function(t){return null==this.filter&&this.x(t)},t.prototype.S=function(t){return f(t,this.w)&&(null==this.filter||this.filter.S(t))},t}(),g=function(){function t(t){this.F=t}return t.prototype.y=function(t){return f(t,this.F)},t.prototype.S=function(t){return t===this.F},t}(),p=function(){function t(t){this.M=RegExp(t.split("*").map(function(t){return(n=t)&&r.test(n)?n.replace(e,"\\$&"):n;var n}).join(".*"))}return t.prototype.y=function(t){return!0},t.prototype.S=function(t){return this.M.test(t)},t.prototype.toString=function(){return this.M.toString()},t}(),y=function(){function t(t,n){var e=this;this.T=t,this.k=n,this.A=[],this.k.forEach(function(t){t instanceof d&&null==t.filter&&e.A.push(t)})}return t.prototype.y=function(t){for(var n=0,e=this.A;n<e.length;n++){if(e[n].m(t))return!1}for(var r=0,i=this.T;r<i.length;r++){if(i[r].y(t))return!0}return!1},t.prototype.S=function(t){for(var n=0,e=this.k;n<e.length;n++){if(e[n].S(t))return!1}for(var r=0,i=this.T;r<i.length;r++){if(i[r].S(t))return!0}return!1},t}();var S,w,x,m,F,M,T={b:function(){return process.hrtime()},I:function(t){var n=process.hrtime(t);return n[0]+n[1]/1e6}},k=require("kotlin-test"),A=new i({s:"0.0.1",u:"kotlin-js-tests",r:"Simple Kotlin/JS tests runner with TeamCity reporter",o:"[-t --tests] [-e --exclude] <module_name1>, <module_name2>, ..",f:{T:{keys:["--tests","--include"],l:"Tests to include. Example: MySuite.test1,MySuite.MySubSuite.*,*unix*,!*windows*",a:"*"},k:{keys:["--exclude"],l:"Tests to exclude. Example: MySuite.test1,MySuite.MySubSuite.*,*unix*"},q:{keys:["--ignoredTestSuites"],l:"How to deal with ignored test suites",g:!0,values:[s,l,c],c:["don't report ignored test suites","useful to speedup large ignored test suites","will cause visiting all inner tests"],a:c}},p:"module_name"}),b=process.argv.slice(2),E=A.parse(b),I={U:E.q||c,T:E.T,k:E.k},q=new t(null,function(t){return console.log(t)}),U={suite:function(t,n,e){n||e()},test:function(t,n,e){n||e()}};S=U,w=I.U,x=q,m=T,F=!1,M=null,n.kotlin.test.setAssertHook_4duqou$(function(t){M=t}),U=function(t,n,e){var r=[],i=[];function u(t,n,e){var r,i,u;(r=t,i=function(t){return t.split(",")},u=[],r.forEach(function(t){i(t).forEach(function(t){u.push(t)})}),u).map(function(t){t.length&&"!"==t[0]?o(e,h(t.substring(1))):o(n,h(t))})}if(u(n,r,i),u(e,i,r),0==r.length&&0==i.length)return t;0==r.length&&r.push(v);var s=new y(r,i);return function(r,i){var u=[];function s(){return u.slice(1).join(".")}return{suite:function(t,n,e){u.push(t);try{if(1<u.length&&!i.y(s()))return;r.suite(t,n,e)}finally{u.pop()}},test:function(t,n,e){u.push(t);try{if(!i.S(s()))return;r.test(t,n,e)}finally{u.pop()}}}}(t,s)}(U={suite:function(t,n,e){if(n){if(w==s)return;if(w==l)return void x.e("testIgnored",a(t,{suite:!0}))}x.e("testSuiteStarted",a(t));var r=F;!r&&n&&(F=!0);try{n&&w==c?e():S.suite(t,n,e)}finally{n&&!r&&(F=!1);var i=a(t);n&&(i.ignored=!0),x.e("testSuiteFinished",i)}},test:function(n,t,e){if(F||t)x.e("testIgnored",a(n));else{var r=m?m.b():null;x.e("testStarted",a(n));try{S.test(n,t,e)}catch(t){var i=a(n,{message:t.message,details:t.stack});M&&(i.type="comparisonFailure",i.expected=M.expected,i.actual=M.actual),x.e("testFailed",i)}finally{M=null,i=a(n),r&&(i.duration=m.I(r)),x.e("testFinished",i)}}}},I.T,I.k),k.setAdapter(U),E.d.forEach(function(t){require(t)});
//# sourceMappingURL=kotlin-test-nodejs-runner.js.map