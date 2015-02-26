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
    $scope.awesomeThings = [
      'HTML5 Boilerplate',
      'AngularJS',
      'Karma'
    ];
    $log.debug("EditCtrl",resolvedDomainBean);
    $scope.resource = resolvedDomainBean.data;
  });
