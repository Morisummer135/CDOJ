cdoj
.directive("list",
  ->
    restrict: "EA"
    replace: true
    transclude: true
    scope:
      condition: "="
      requestUrl: "@"
    controller: "ListController"
    template: """
<div>
  <div class="col-md-12">
    <pagination total-items="pageInfo.totalItems"
                items-per-page="itemsPerPage"
                page="condition.currentPage"
                max-size="showPages"
                class="pagination-sm"
                boundary-links="true"
                previous-text="&lsaquo;"
                next-text="&rsaquo;"
                first-text="&laquo;"
                last-text="&raquo;"></pagination>
  </div>
  <div class="col-md-12" ng-transclude></div>
</div>
"""
  )