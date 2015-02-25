'use strict';

/**
 * @ngdoc function
 * @name generationApp.controller:MainCtrl
 * @description # MainCtrl Controller of the generationApp
 */
angular.module('generationApp').controller('MainCtrl', function($scope) {
  $scope.awesomeThings = ['HTML5 Boilerplate', 'AngularJS', 'Karma'];

  $scope.listBean = ['TuBean', 'toto'];
});
