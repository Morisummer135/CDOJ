cdoj
.controller("ListController", [
    "$scope", "$rootScope", "$http", "$window", "$routeParams"
    ($scope, $rootScope, $http, $window, $routeParams) ->
      $scope.pageInfo =
        countPerPage: 20
        currentPage: 1
        displayDistance: 2
        totalPages: 1
      $scope.itemsPerPage = 20
      $scope.showPages = 10

      _.each $scope.condition, (val, key)->
        $scope.condition[key] = $routeParams[key] if angular.isDefined $routeParams[key]
      $scope.refresh = ->
        if $scope.requestUrl != 0
          condition = angular.copy($scope.condition)
          $http.post($scope.requestUrl, condition).then (response) =>
            data = response.data
            if data.result == "success"
              $scope.$$nextSibling.list = response.data.list
              $scope.pageInfo = data.pageInfo
            else
              $window.alert data.error_msg
      $rootScope.$watch("currentUser", ->
        $scope.refresh()
      , true)
      $scope.$watch("condition", ->
        $scope.refresh()
      , true
      )
  ])