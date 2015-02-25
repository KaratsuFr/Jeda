'use strict';

/**
 * @ngdoc function
 * @name JedaApp.controller:MainCtrl
 * @description # MainCtrl Controller of the JedaApp
 */
angular.module('JedaApp').controller('MainCtrl', function($scope, resolvedBaseDomainBean, ContextBean,$log,$location) {
  $scope.awesomeThings = ['HTML5 Boilerplate', 'AngularJS', 'Karma'];

  $log.debug(resolvedBaseDomainBean);
  $scope.listBean = resolvedBaseDomainBean.data;
  $log.debug(resolvedBaseDomainBean);
  
  $scope.goToListForBean = function(beanName) {
    ContextBean.addSonCurrBean(beanName, null);
    $log.debug(ContextBean);
    $location.path('/search');
  }
});
