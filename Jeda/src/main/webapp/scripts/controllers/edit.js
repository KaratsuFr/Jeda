'use strict';

/**
 * @ngdoc function
 * @name JedaApp.controller:EditCtrl
 * @description
 * # EditCtrl
 * Controller of the JedaApp
 */
angular.module('JedaApp')
  .controller('EditCtrl', function ($scope,$log,resolvedDomainBean) {    
    $log.debug("EditCtrl",resolvedDomainBean);
    $scope.resource = resolvedDomainBean.data;
  });
