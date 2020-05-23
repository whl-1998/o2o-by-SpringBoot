$(function() {
    var loading = false;
    var maxItems = 20;
    var pageSize = 10;
    var listUrl = '/o2o/frontend/listproductcar';// 获取该用户的奖品领取记录列表的URL
    var pageNum = 1;
    var productName = '';


    addItems(pageSize, pageNum);
    function addItems(pageSize, pageIndex) {
        var url = listUrl + '?pageIndex=' + pageIndex + '&pageSize=' + pageSize + '&productName=' + productName;
        loading = true;
        $.getJSON(url, function(data) {
            if (data.success) {
                maxItems = data.count;
                var html = '';
                data.productCarMapList.map(function(item) {
                    var status = '';
                    var buy_status = '';
                    var qrcodeUrl = "/o2o/frontend/generateqrcode4product?productId=" + item.product.productId; //获取二维码的URL
                    // 根据usedStatus显示是否已在实体店领取过奖品
                    if (item.status == 0) {
                        status = "待支付";
                        buy_status = '点击付款'
                    } else if (item.status == 1) {
                        status = "已购买";
                        buy_status = '再次购买'
                    }
                    html += ''
                    + '<div class="card" data-user-award-id=' + item.productCarId + '>'
                        + '<div class="card-header">'  //+ item.shop.shopName
                            + '<span class="pull-right">' + status + '</sapn>'
                        + '</div>'
                        + '<div class="card-content">'
                            + '<div class="list-block media-list">'
                            + '<ul>'
                                + '<li class="item-content">'
                                    + '<div class="item-inner">'
                                        + '<div class="item-subtitle">' + item.product.productName + '</div>'
                                    + '</div>'
                                + '</li>'
                            + '</ul>'
                            + '</div>'
                        + '</div>'
                        + '<div class="card-footer">'
                            + '<p class="color-gray">' + new Date(item.createTime).Format("yyyy-MM-dd") + '</p>'
                        + '</div>'
                        + '<div class="card-btn">'
                            + '<a href=' + qrcodeUrl + ' class="button" onclick="">' + buy_status + '</a>'
                        + '</div>'
                    + '</div>';
                });


                $('.list-div').append(html);
                var total = $('.list-div .card').length;
                if (total >= maxItems) {
                    // 加载完毕，则注销无限加载事件，以防不必要的加载
                    $.detachInfiniteScroll($('.infinite-scroll'));
                    // 删除加载提示符
                    $('.infinite-scroll-preloader').remove();
                    return;
                }
                pageNum += 1;
                loading = false;
                $.refreshScroller();
            }
        });
    }

    // 绑定卡片点击的事件
    // 若点击卡片, 进入商品支付逻辑
    $('.list-div').on('click', '.card', function(e) {
        // var userAwardId = e.currentTarget.dataset.userAwardId;
        // window.location.href = '/o2o/frontend/myawarddetail?userAwardId=' + userAwardId;
    });

    // 无极滚动
    $(document).on('infinite', '.infinite-scroll-bottom', function() {
        if (loading)
            return;
        addItems(pageSize, pageNum);
    });

    // 绑定搜索事件，主要是传入奖品名进行模糊查询
    $('#search').on('change', function(e) {
        productName = e.target.value;
        $('.list-div').empty();
        pageNum = 1;
        addItems(pageSize, pageNum);
    });

    // 侧边栏按钮事件绑定
    $('#me').click(function() {
        $.openPanel('#panel-right-demo');
    });

    $.init();
});