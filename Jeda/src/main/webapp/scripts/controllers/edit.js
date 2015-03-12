'use strict';

/**
 * @ngdoc function
 * @name JedaApp.controller:EditCtrl
 * @description # EditCtrl Controller of the JedaApp
 */
angular.module('JedaApp').controller('EditCtrl', function($scope, $log, resolvedDomainBean, $filter) {
  $log.debug("EditCtrl", resolvedDomainBean);
  $scope.resource = resolvedDomainBean.data;

  if ($scope.resource.lstValues[0] == undefined) {
    $scope.$emit("label.jeda.invalid.data");
  }
 
  for (var i = 0; i < $scope.resource.lstValues[0].length; i++) {
    // format some value temporary:
    if ($scope.resource.lstFieldInfo[i].jsType == "date") {
      $scope.resource.lstValues[0][i].value = new Date($scope.resource.lstValues[0][i].value); // $filter('date')($scope.resource.lstValues[0][i].value,
      // "dd/MM/yyyy");
    }
  }

});
