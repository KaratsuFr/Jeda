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
          }, {
            total: $scope.resource.totalNbResult, // length of data
            getData: function($defer, params) {

              // $log.debug("reader tab with params:", params);
              params.total($scope.resource.totalNbResult); // set total for recalc pagination

              // use build-in angular filter
              var filteredData = params.filter() ? $filter('filter')($scope.resource.lstDomain, params.filter())
                      : $scope.resource.lstDomain;
              // use build-in angular filter
              var orderedData = params.sorting() ? $filter('orderBy')(filteredData, params.orderBy()) : filteredData;
              $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
            }
          });

          $scope.cols = [];
          for (var colIndex = 0; colIndex < $scope.resource.lstFieldInfo.length; colIndex++) {
            var fieldInfo = $scope.resource.lstFieldInfo[colIndex];
            var col = {
              title: fieldInfo.label,
              titleAlt: fieldInfo.description,
              sortable: fieldInfo.jsName,
              filter: {},
              show: fieldInfo.displayList,
              field: fieldInfo.jsName
            };
            col.filter[fieldInfo.jsName] = 'text';
            $scope.cols.push(col);
          }
        });
