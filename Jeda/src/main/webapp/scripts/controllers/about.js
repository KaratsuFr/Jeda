'use strict';

/**
 * @ngdoc function
 * @name generationApp.controller:AboutCtrl
 * @description
 * # AboutCtrl
 * Controller of the generationApp
 */
angular.module('generationApp')
  .controller('AboutCtrl', function ($scope) {
    $scope.awesomeThings = [
      'HTML5 Boilerplate',
      'AngularJS',
      'Karma'
    ];
  });
