'use strict';

/**
 * @ngdoc service
 * @name JedaApp.genericRest
 * @description
 * # genericRest
 * Service in the JedaApp.
 */
angular.module('JedaApp')
  .service('ContextBean', function () {
    // AngularJS will instantiate a singleton by calling "new" on this function
    var contextBean = {data: [{"beanName":"","id":""}]};
    
    contextBean.getData = function(){
      return contextBean.data;
    };
    
    contextBean.getCurrBean = function(){
      return contextBean.data[contextBean.data.length -1];
    };
    
    contextBean.popBean = function(){
      contextBean.data.pop();
    };
    contextBean.setCurrBean = function(beanName, beanId){
      contextBean.data.pop();
      contextBean.data.push({"beanName":beanName,"id":beanId});
    };    
    contextBean.addSonCurrBean = function(beanName, beanId){
      contextBean.data.push({"beanName":beanName,"id":beanId});
    };
    
    return contextBean;
  });
