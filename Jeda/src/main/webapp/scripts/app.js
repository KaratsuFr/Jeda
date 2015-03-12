'use strict';

/**
 * @ngdoc overview
 * @name JedaApp
 * @description # JedaApp Main module of the application.
 */
angular.module(
        'JedaApp',
        ['ngAnimate', 'ngAria', 'ngCookies', 'ngMessages', 'ngResource', 'ngRoute', 'ngSanitize', 'ngTouch',
            'pascalprecht.translate', 'ngTable', 'mgcrea.ngStrap', 'mgcrea.ngStrap.aside', 'mgcrea.ngStrap.tooltip',
            'tmh.dynamicLocale', 'angular-growl']).config(
        function($routeProvider, $translateProvider, tmhDynamicLocaleProvider) {
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
                if (id == undefined || id == null) {
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
          $translateProvider.preferredLanguage('en');
          $translateProvider.fallbackLanguage('en');

          $translateProvider.useCookieStorage();
          tmhDynamicLocaleProvider.useCookieStorage('NG_TRANSLATE_LANG_KEY');
          tmhDynamicLocaleProvider.localeLocationPattern("bower_components/angular-i18n/angular-locale_{{locale}}.js");
          tmhDynamicLocaleProvider.useStorage('$cookieStore');

        }).run(
        function($log, $location, $rootScope, $routeParams, tmhDynamicLocale, $locale, $translate,
                $translateCookieStorage, $http, growl) {
          var lang = window.navigator.language;
          $translate.use(lang);

          tmhDynamicLocale.set($translate.use());

          $rootScope.goToListForBean = function(beanName) {
            $location.path('/' + beanName + '/search');
          }

          $rootScope.viewBean = function(beanId) {
            $location.path('/' + $routeParams.beanName + '/edit').search({
              id: beanId
            });
          };

          $rootScope.back = function() {
            // suppress param
            $location.url($location.path());
            if ($rootScope.tabBreadcrumb.length <= 2) {
              var targetDest = '/';
            } else {
              // length - 1 because array start at 0 and - 2 to skip current view
              var targetDest = $rootScope.tabBreadcrumb[$rootScope.tabBreadcrumb.length - 2].href;
              targetDest = targetDest.substring(1, targetDest.length);
            }
            $log.debug("back go to:", targetDest, $rootScope.tabBreadcrumb);
            $location.path(targetDest);
          }

          $rootScope.createBean = function() {
            $location.path('/' + $routeParams.beanName + '/edit').search({
              create: ''
            });
          };

          $rootScope.createOrUpdate = function(resourceDto) {
            $http.post('rest/entity/' + $routeParams.beanName, resourceDto).success(function(data, status) {
              growl.success("label.jeda.save.ok");
              $rootScope.back();
            }).error(function(data, status) {
              growl.error("label.jeda.save.ko");
            });
          }

          $rootScope.$on("$routeChangeSuccess", function(data, route, routeParams) {
            $rootScope.currBean = route.params.beanName;

            // build tabBreadcrumb [{href:"/Bean/search..",label:""}]

            $log.debug("$routeChangeSuccess", $location, route, routeParams);
            var path = $location.path();
            var tabPath = path.split("/");
            $rootScope.tabBreadcrumb = [{}];

            var baseHref = '#';
            var parentId = null;
            for (var index = 1; index < (tabPath.length); index++) {

              var currPath = tabPath[index];
              var id = $location.search()[currPath];

              baseHref += "/" + currPath;
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
              }
              
              
              if (tabPath.length == (index + 1)) {
                breadC["class"] = "active";
                breadC["href"] = null;
              }

              $rootScope.tabBreadcrumb.push(breadC);
              
              // if last is search remove pop 1 item
              if (tabPath.length == (index + 1) && breadC.label== 'search') {
                $rootScope.tabBreadcrumb.pop();
                var lastSearchBreadC = $rootScope.tabBreadcrumb.pop();
                lastSearchBreadC.href=null;
                lastSearchBreadC["class"] = "active";
                $rootScope.tabBreadcrumb.push(lastSearchBreadC);

              }
            }
          });

          $rootScope.$on("$routeChangeError", function($route) {
            $location.url($location.path());
            $location.path('/');
          });
        });
