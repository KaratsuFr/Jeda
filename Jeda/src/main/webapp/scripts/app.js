'use strict';

/**
 * @ngdoc overview
 * @name JedaApp
 * @description
 * # JedaApp
 *
 * Main module of the application.
 */
angular
  .module('JedaApp', [
    'ngAnimate',
    'ngAria',
    'ngCookies',
    'ngMessages',
    'ngResource',
    'ngRoute',
    'ngSanitize',
    'ngTouch','pascalprecht.translate','ngTable','mgcrea.ngStrap', 'mgcrea.ngStrap.aside', 'mgcrea.ngStrap.tooltip' 
  ])
  .config(function ($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'views/main.html',
        resolve: {          
          resolvedBaseDomainBean: ['$http', function($http) {
            return $http.get('rest/app');
          }],
        },
        controller: 'MainCtrl'
      })
      .when('/about', {
        templateUrl: 'views/about.html',
        controller: 'AboutCtrl'
      })
      .when('/search', {
        templateUrl: 'views/search.html',
        controller: 'SearchCtrl'
      })
      .when('/edit', {
        templateUrl: 'views/edit.html',
        controller: 'EditCtrl'
      })
      .otherwise({
        redirectTo: '/'
      });
  });
