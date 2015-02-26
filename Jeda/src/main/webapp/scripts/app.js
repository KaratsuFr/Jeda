'use strict';

/**
 * @ngdoc overview
 * @name JedaApp
 * @description # JedaApp Main module of the application.
 */
angular.module(
        'JedaApp',
        ['ngAnimate', 'ngAria', 'ngCookies', 'ngMessages', 'ngResource', 'ngRoute', 'ngSanitize', 'ngTouch',
            'pascalprecht.translate', 'ngTable', 'mgcrea.ngStrap', 'mgcrea.ngStrap.aside', 'mgcrea.ngStrap.tooltip'])
        .config(function($routeProvider) {
          $routeProvider.when('/', {
            templateUrl: 'views/main.html',
            resolve: {
              resolvedBaseDomainBean: ['$http', function($http) {
                return $http.get('rest/app');
              }],
            },
            controller: 'MainCtrl'
          }).when('/about', {
            templateUrl: 'views/about.html',
            controller: 'AboutCtrl'
          }).when('/search', {
            templateUrl: 'views/search.html',
            controller: 'SearchCtrl',
            resolve: {
              resolvedSearchBean: ['$http','ContextBean', function($http,ContextBean) {
                // TODO USE SERVICE 
               return $http.get('rest/entity/' + ContextBean.getCurrBean().beanName) ;
              }]
            }
          }).when('/edit', {
            templateUrl: 'views/edit.html',
            controller: 'EditCtrl',
            resolve: {
              resolvedDomainBean: ['$http','ContextBean', function($http,ContextBean) {
                // TODO USE SERVICE 
                var id =  ContextBean.getCurrBean().id;
                var params = "";
                if(id == undefined || id == ""){
                  params="?create";
                }else{
                  params= "?id="+ id;
                }
                return $http.get('rest/entity/' + ContextBean.getCurrBean().beanName + params );
              }]
            }
          }).otherwise({
            redirectTo: '/'
          });
        });
