'use strict';

/**
 * @ngdoc function
 * @name JedaApp.controller:MainCtrl
 * @description # MainCtrl Controller of the JedaApp
 */
angular.module('JedaApp').controller('MainCtrl', function($scope,resolvedBaseDomainBean) {
  $scope.awesomeThings = ['HTML5 Boilerplate', 'AngularJS', 'Karma'];

  $scope.listBean = resolvedBaseDomainBean.data;
  
  $scope.contextBean = [{"beanName":"","id":""}];
  
  $scope.listBean = function(beanName){
    
  }
});
