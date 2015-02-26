'use strict';

/**
 * @ngdoc function
 * @name JedaApp.controller:SearchCtrl
 * @description # SearchCtrl Controller of the JedaApp
 */
angular.module('JedaApp').controller('SearchCtrl', function($scope, ContextBean, $log, $http,ngTableParams,$filter) {
  $log.debug("search ctl", ContextBean);
  $scope.currBean = ContextBean.getCurrBean().beanName;

  $scope.resource = {lstValues:[]};

  $http.get('rest/entity/' + $scope.currBean).success(function(result) {
    $log.debug("result service entity" + $scope.currBean, result);
    $scope.resource = result;
    $scope.tableParams.reload();

  }).error(function(error) {
    $log.debug("error service entity" + $scope.currBean, error);
  });

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
      var orderedData = params.sorting() ? $filter('orderBy')($scope.resource.lstValues, params.orderBy()) : $scope.resource.lstValues;
      $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
      

    }

  });
});
