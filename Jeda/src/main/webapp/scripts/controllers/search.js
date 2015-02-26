'use strict';

/**
 * @ngdoc function
 * @name JedaApp.controller:SearchCtrl
 * @description # SearchCtrl Controller of the JedaApp
 */
angular.module('JedaApp').controller(
        'SearchCtrl',
        function($scope, ContextBean, $log, $http, ngTableParams, $filter, $location, resolvedSearchBean) {
          $log.debug("search ctl", ContextBean);
          $scope.currBean = ContextBean.getCurrBean().beanName;
          $scope.resource = resolvedSearchBean.data;

          $scope.viewBean = function(beanId) {
            ContextBean.setCurrBean(ContextBean.getCurrBean().beanName, beanId);
            $log.debug("view bean id:", ContextBean.getCurrBean());
            $location.path('/edit');
          };

          $scope.tableParams = new ngTableParams({
            page: 1, // show first page
            count: 10
          // count per page

          }, {
            total: $scope.resource.lstValues.length, // length of data
            getData: function($defer, params) {

              $log.debug("reader tab with params:", params);

              params.total($scope.resource.lstValues.length); // set total for recalc pagination

              // use build-in angular filter
              var orderedData = params.sorting() ? $filter('orderBy')($scope.resource.lstValues, params.orderBy())
                      : $scope.resource.lstValues;
              $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));

            }
          });
        });
