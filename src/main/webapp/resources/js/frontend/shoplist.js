$(function() {
    var loading = false;
    var maxItems = 999;// 分页允许返回的最大条数，超过此数则禁止访问后台
    var pageSize = 3;// 一页返回的最大条数
    var listUrl = '/o2o/frontend/listshops';// 获取店铺列表的URL
    var searchDivUrl = '/o2o/frontend/listshopspageinfo';// 获取店铺类别列表以及区域列表的URL
    var pageNum = 1;// 页码
    var parentId = getQueryString('parentId');// 从地址栏URL里尝试获取parent shop category id.
    var selectedParent = false;// 是否选择了子类
    if (parentId){
        selectedParent = true;
    }
    var areaId = '';
    var shopCategoryId = '';
    var shopName = '';
    getSearchDivData();// 渲染出店铺类别列表以及区域列表以供搜索
    addItems(pageSize, pageNum);// 预先加载10条店铺信息

    /**
     * 获取店铺类别列表以及区域列表信息
     * @returns
     */
    function getSearchDivData() {
        // 如果传入了parentId，则取出此一级类别下面的所有二级类别
        var url = searchDivUrl + '?' + 'parentId=' + parentId;
        $.getJSON(url, function(data) {
            if (data.success) {
                var shopCategoryList = data.shopCategoryList;// 获取后台返回过来的店铺类别列表
                var html = '';
                html += '<a href="#" class="button" data-category-id=""> 全部类别  </a>';
                shopCategoryList.map(function(item) {// 遍历店铺类别列表，拼接出a标签集
                    html += '<a href="#" class="button" data-category-id=' + item.shopCategoryId + '>' + item.shopCategoryName + '</a>';
                });
                $('#shoplist-search-div').html(html);// 将拼接好的类别标签嵌入前台的html组件里
                var selectOptions = '<option value="">全部街道</option>';

                var areaList = data.areaList;// 获取后台返回过来的区域信息列表
                areaList.map(function(item) {// 遍历区域信息列表，拼接出option标签集
                    selectOptions += '<option value="' + item.areaId + '">' + item.areaName + '</option>';
                });
                // 将标签集添加进area列表里
                $('#area-search').html(selectOptions);
            }
        });
    }

    /**
     * 获取分页展示的店铺列表信息
     * @param pageSize
     * @param pageIndex
     * @returns
     */
    function addItems(pageSize, pageIndex) {
        // 拼接出查询的URL，赋空值默认就去掉这个条件的限制，有值就代表按这个条件去查询
        var url = listUrl + '?' + 'pageIndex=' + pageIndex + '&pageSize='
            + pageSize + '&parentId=' + parentId + '&areaId=' + areaId
            + '&shopCategoryId=' + shopCategoryId + '&shopName=' + shopName;
        loading = true;// 设定加载符，若还在后台取数据则不能再次访问后台，避免多次重复加载
        // 访问后台获取相应查询条件下的店铺列表
        $.getJSON(url, function(data) {
            if (data.success) {
                maxItems = data.count;// 获取当前查询条件下店铺的总数
                var html = '';
                data.shopList.map(function(item) { // 遍历店铺列表，拼接出卡片集合
                    html += '' + '<div class="card" data-shop-id="'
                        + item.shopId + '">' + '<div class="card-header">'
                        + item.shopName + '</div>'
                        + '<div class="card-content">'
                        + '<div class="list-block media-list">' + '<ul>'
                        + '<li class="item-content">'
                        + '<div class="item-media">' + '<img src="'
                        + getContextPath() + item.shopImg + '" width="44">' + '</div>'
                        + '<div class="item-inner">'
                        + '<div class="item-subtitle">' + item.shopDesc
                        + '</div>' + '</div>' + '</li>' + '</ul>'
                        + '</div>' + '</div>' + '<div class="card-footer">'
                        + '<p class="color-gray">'
                        + new Date(item.updateTime).Format("yyyy-MM-dd")
                        + '更新</p>' + '<span>点击查看</span>' + '</div>'
                        + '</div>';
                });
                $('.list-div').append(html);// 将卡片集合添加到目标HTML组件里
                var total = $('.list-div .card').length;// 获取目前为止已显示的卡片总数，包含之前已经加载的
                if (total >= maxItems) {// 若总数达到跟按照此查询条件列出来的总数一致，则停止后台的加载
                    $('.infinite-scroll-preloader').hide();// 隐藏提示符
                } else {
                    $('.infinite-scroll-preloader').show();
                }
                pageNum += 1;// 否则页码加1，继续load出新的店铺
                loading = false;// 加载结束，可以再次加载了
                $.refreshScroller();// 刷新页面，显示新加载的店铺
            }
        });
    }

    // 下滑屏幕自动进行分页搜索
    $(document).on('infinite', '.infinite-scroll-bottom', function() {
        if (loading)
            return;
        addItems(pageSize, pageNum);
    });

    // 点击店铺的卡片进入该店铺的详情页
    $('.shop-list').on('click', '.card', function(e) {
        var shopId = e.currentTarget.dataset.shopId;
        window.location.href = '/o2o/frontend/shopdetail?shopId=' + shopId;
    });

    // 选择新的店铺类别之后，重置页码，清空原先的店铺列表，按照新的类别去查询
    $('#shoplist-search-div').on('click', '.button', function(e) {
        if (parentId && selectedParent) {// 如果传递过来的是一个父类下的子类
            shopCategoryId = e.target.dataset.categoryId;
            if ($(e.target).hasClass('button-fill')) {// 若之前已选定了别的category,则移除其选定效果，改成选定新的
                $(e.target).removeClass('button-fill');
                shopCategoryId = '';
            } else {
                $(e.target).addClass('button-fill').siblings().removeClass('button-fill');
            }
            $('.list-div').empty();// 由于查询条件改变，清空店铺列表再进行查询
            pageNum = 1;// 重置页码
            addItems(pageSize, pageNum);
        } else { // 如果传递过来的父类为空，则按照父类查询
            parentId = e.target.dataset.categoryId;
            if ($(e.target).hasClass('button-fill')) {
                $(e.target).removeClass('button-fill');
                parentId = '';
            } else {
                $(e.target).addClass('button-fill').siblings().removeClass('button-fill');
            }
            $('.list-div').empty();// 由于查询条件改变，清空店铺列表再进行查询
            pageNum = 1;// 重置页码
            addItems(pageSize, pageNum);
        }
    });

    // 需要查询的店铺名字发生变化后，重置页码，清空原先的店铺列表，按照新的名字去查询
    $('#search').on('change', function(e) {
        shopName = e.target.value;
        $('.list-div').empty();
        pageNum = 1;
        addItems(pageSize, pageNum);
    });

    // 区域信息发生变化后，重置页码，清空原先的店铺列表，按照新的区域去查询
    $('#area-search').on('change', function() {
        areaId = $('#area-search').val();
        $('.list-div').empty();
        pageNum = 1;
        addItems(pageSize, pageNum);
    });

    $('#me').click(function() {// 点击后打开右侧栏
        $.openPanel('#panel-right-demo');
    });

    $.init();// 初始化页面
});