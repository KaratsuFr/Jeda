'use strict';

/**
 * @ngdoc function
 * @name JedaApp.controller:MainCtrl
 * @description # MainCtrl Controller of the JedaApp
 */
angular.module('JedaApp').controller('MainCtrl',
        function($scope, resolvedBaseDomainBean, $log, $location, $rootScope, $routeParams) {

          $log.debug(resolvedBaseDomainBean);
          $scope.listBean = resolvedBaseDomainBean.data;
          $log.debug(resolvedBaseDomainBean);

        });
