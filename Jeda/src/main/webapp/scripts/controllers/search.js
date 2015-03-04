'use strict';

/**
 * @ngdoc function
 * @name JedaApp.controller:SearchCtrl
 * @description # SearchCtrl Controller of the JedaApp
 */
angular.module('JedaApp').controller(
        'SearchCtrl',
        function($scope, $log, $http, ngTableParams, $filter, $location, resolvedSearchBean, $routeParams) {
          
          $scope.resource = resolvedSearchBean.data;

          $scope.tableParams = new ngTableParams({
            page: 1, // show first page
            count: 10
          // count per page

          }, {
            total: $scope.resource.totalNbResult, // length of data
            getData: function($defer, params) {

              $log.debug("reader tab with params:", params);
              params.total($scope.resource.totalNbResult); // set total for recalc pagination

              // use build-in angular filter
              var orderedData = params.sorting() ? $filter('orderBy')($scope.resource.lstValues, params.orderBy())
                      : $scope.resource.lstValues;
              $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));

            }
          });
        });
