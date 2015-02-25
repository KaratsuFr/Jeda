'use strict';

/**
 * @ngdoc function
 * @name generationApp.controller:SearchCtrl
 * @description
 * # SearchCtrl
 * Controller of the generationApp
 */
angular.module('generationApp')
  .controller('SearchCtrl', function ($scope) {
    $scope.awesomeThings = [
      'HTML5 Boilerplate',
      'AngularJS',
      'Karma'
    ];
  });
