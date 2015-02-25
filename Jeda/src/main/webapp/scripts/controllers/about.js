'use strict';

/**
 * @ngdoc function
 * @name JedaApp.controller:AboutCtrl
 * @description
 * # AboutCtrl
 * Controller of the JedaApp
 */
angular.module('JedaApp')
  .controller('AboutCtrl', function ($scope) {
    $scope.awesomeThings = [
      'HTML5 Boilerplate',
      'AngularJS',
      'Karma'
    ];
  });
