'use strict';

/**
 * @ngdoc function
 * @name JedaApp.controller:EditCtrl
 * @description # EditCtrl Controller of the JedaApp
 */
angular.module('JedaApp').controller('EditCtrl', function($scope, $log, resolvedDomainBean, $filter) {
  $log.debug("EditCtrl", resolvedDomainBean);
  $scope.resource = resolvedDomainBean.data;

  if (resolvedDomainBean.status != 200) {
    $scope.$emit("jedaError", {
      title: "label.jeda.invalid.data",
      message: resolvedDomainBean.status
    });
  } else {
    for (var i = 0; i < $scope.resource.lstFieldInfo.length; i++) {
      // format some value temporary:
      if ($scope.resource.lstFieldInfo[i].jsType == "date") {
        var fieldName = $scope.resource.lstFieldInfo[i].jsName;
        $scope.resource.lstDomain[0][fieldName] = new Date($scope.resource.lstDomain[0][fieldName]);
      }
    }
  }

});
