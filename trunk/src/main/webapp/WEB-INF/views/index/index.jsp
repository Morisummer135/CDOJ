<%--
	Index pages
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>CDOJ</title>
</head>
<body>
<div id="cdoj-index">
  <div class="row">
    <div class="col-md-12">
      <div class="hero">
        <div class="hero-titles">
          <h1 class="cdoj-codefont">CDOJ</h1>

          <h2>stO Ruinshe Orz</h2>

          <div class="alert alert-danger">
            加密算法略有修改，部分用户登陆可能提示密码错误，自行找回密码即可。
          </div>
        </div>
      </div>
    </div>
    <div id="article-list"
         ng-controller="ListController"
         ng-init="condition={
          currentPage: null,
          startId: undefined,
          endId: undefined,
          keyword: undefined,
          title: undefined,
          orderFields: 'id',
          orderAsc: 'false'
         };
         requestUrl='/article/search';">
      <div class="col-md-12">
        <div ui-page-info
             page-info="pageInfo"
             condition="condition"
             id="page-info">
        </div>
      </div>
      <div class="col-md-12">
        <div class="cdoj-article" ng-repeat="article in list">
          <h1><a href="/article/show/{{article.articleId}}"
                 target="_blank"
                 ng-bind="article.title"></a>
          </h1>
          <small>
            <span ng-bind="article.clicked"></span> visited, create by
            <span ng-bind="article.ownerName"></span>, last modified at
            <span class="cdoj-article-post-time"
                  ui-time
                  time="article.time"
                  inline="true"></span>
          </small>
          <div class="cdoj-article-summary">
            <div class="cdoj-article-summary-content" ui-markdown content="article.content">
            </div>
          </div>
          <p ui-readmore
             has-more="article.hasMore"
             article-id="article.articleId"></p>
          <hr/>
        </div>
      </div>
    </div>
  </div>
</div>
</body>
</html>
