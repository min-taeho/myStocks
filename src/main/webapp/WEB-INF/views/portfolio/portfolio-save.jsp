<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/template/include/taglibInc.jsp" %>

    <!-- 입력 영역 -->
    <div class="col-md-4">
        <form>
            <div class="form-group">
                <label for="title">티커</label>
                <input type="text" class="w-50 form-control" id="ticker" placeholder="티커를 입력하세요">
            </div>
        </form>
        <a href="/portfolio/v1" role="button" class="btn btn-secondary">취소</a>
        <button type="button" class="btn btn-primary" id="btn-save">등록</button>
    </div>
    <script src="/js/app/portfolio/portfolio.js"></script>
