var inputHtmlTpl = "<label class='control-label'  translate='{{jdInfo.label}}'></label>"
        + "<input type='{{jdInfo.jsType}}'ng-readonly='jdInfo.jsName == \"_id\"'  class='form-control' ng-model='jdModel'></input>";

var wipHhtmlTpl = "<label> WIP </label>";

var linkHtmlTpl = "<label class='control-label' translate='{{jdInfo.label}}'></label>"
        + "<ul class='list-group'> <li ng-repeat='link in jdModel.links' class='text-center list-group-item' ng-click='goToUrl(link.uri)'>{{'label.jeda.viewDomain' | translate}} : {{link.rel | translate}}</li></ul>";

var dateHtmlTpl = "<div class='form-group'ng-class=\"{'has-error': datepickerForm.date.$invalid}\">"
        + "<i class='glyphicon glyphicon-calendar'></i>"
        + "<label class='control-label' translate='{{jdInfo.label}}'> </label> <input class='form-control' ng-model='jdModel'  name='{{jdInfo.jsName}}' bs-datepicker type='text'> </div>";

angular.module('JedaApp').directive('jdField', function($log, $location, $compile) {

  var getTemplate = function(jdInfo) {
    var template = '';

    if (jdInfo.link) {
      template = linkHtmlTpl;
    } else {
      switch (jdInfo.jsType) {
      case 'text':
        template = inputHtmlTpl;
        break;
      case 'checkbox':
        template = wipHhtmlTpl;
        break;
      case 'date':
        template = dateHtmlTpl;
        break;
      default:
        $log.debug("jeda Field unknown type: ", jdInfo.jsType);
        template = inputHtmlTpl;
      }
    }
    return template;
  }

  var linker = function(scope, element, attrs) {
    var tpl = getTemplate(scope.jdInfo);
    //$log.debug("tpl", tpl, element, attrs);
    element.html(tpl);
    $compile(element.contents())(scope);
  }

  return {
    restrict: 'E',
    replace: true,
    required: ['^jdInfo', '^jdModel'],
    scope: {
      jdInfo: '=',
      jdModel: '=',
    },
    controller: function($scope) {
      $scope.goToUrl = function(uri) {
        // TODO stack to breadCrumb
        $log.debug("go to ", uri);
        $location.url(uri);
      };
    },
    link: linker
  };
});