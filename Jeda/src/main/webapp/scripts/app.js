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
        .config(function($routeProvider, $translateProvider) {
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
          }).when('/:beanName*/search', {
            templateUrl: 'views/search.html',
            controller: 'SearchCtrl',
            resolve: {
              resolvedSearchBean: ['$http', '$route', '$log', function($http, $route, $log) {
                // TODO USE SERVICE
                return $http.get('rest/entity/' + $route.current.params.beanName);
              }]
            }
          }).when('/:beanName*/edit', {
            templateUrl: 'views/edit.html',
            controller: 'EditCtrl',
            resolve: {
              resolvedDomainBean: ['$http', '$route', function($http, $route) {
                // TODO USE SERVICE
                var id = $route.current.params.id;
                var params = "";
                if (id == undefined || id == "") {
                  params = "?create";
                } else {
                  params = "?id=" + id;
                }
                return $http.get('rest/entity/' + $route.current.params.beanName + params);
              }]
            }
          }).otherwise({
            redirectTo: '/'
          });

          // Initialize angular-translate
          $translateProvider.useStaticFilesLoader({
            prefix: 'i18n/',
            suffix: '.json'
          });
          // $translateProvider.preferredLanguage('fr');
          $translateProvider.useCookieStorage();
        }).run(function($log, $location, $rootScope, $routeParams) {

          $rootScope.goToListForBean = function(beanName) {
            $location.path('/' + beanName + '/search');
          }

          $rootScope.viewBean = function(beanId) {
            $location.path('/' + $routeParams.beanName + '/edit').search({
              id: beanId
            });
          };

          $rootScope.createBean = function() {
            $location.path('/' + $routeParams.beanName + '/edit').search({
              create: ''
            });
          };

          $rootScope.$on("$routeChangeSuccess", function(data, route, routeParams) {
            $rootScope.currBean = route.params.beanName;
            
            // build tabBreadcrumb [{href:"/Bean/search..",label:""}]
            
            $log.debug("$routeChangeSuccess", $location, route, routeParams);
            var path = $location.path();
            var tabPath = path.split("/");
            $rootScope.tabBreadcrumb = [{}];

            var baseHref = '#';
            var parentId = null;
            for (var index=1; index< (tabPath.length); index++) {

              var currPath = tabPath[index];
              var id = $location.search()[currPath];

              baseHref += "/" +currPath;
              var currHref = baseHref;

              if (id != null) {
                currHref += "/edit?id=" + id;
                if (parentId != null) {
                  currHref += "&" + parentId;
                }
                parentId = currPath + "=" + id;
              } else {
                currHref += "/search";
                if (parentId != null) {
                  currHref += "?" + parentId;
                }
              }

              var breadC = {
                "href": currHref,
                "label": currPath,
              };
              if(tabPath.length == (index+1)){
                breadC["class"]="active";
                breadC["href"]=null;
              }

              $rootScope.tabBreadcrumb.push(breadC);
            }
          });

          $rootScope.$on("$routeChangeError", function($route) {
            $location.path('/').search();
          });
        });
