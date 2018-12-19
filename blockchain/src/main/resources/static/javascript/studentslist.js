var classname = "";

//var phone = $("#phone").val();
var phone='13100000000';
var pageNum = 1;

var pageSize = 10;

//ajax获取后台数据
function initTable() {

    var tbody="";
    var datas = {
        'teacherphone' : phone,
        'classname':classname,
        'pageNum':pageNum,
        'pageSize':pageSize
    };
    $.ajax({
        type: 'post',
        dataType : "json",
        async: false,
        url: "/student/studentlist",//请求的action路径页面
        data:JSON.stringify(datas),
        error: function () {//请求失败处理函数
            alert('请求失败');
        },
        success:function(data){ //请求成功后处理函数。
            $.each(data.list, function(i, n) {
                var trs = "";
                trs += "<tr><td width='15%'>" + n.getKg_fullname() + "</td><td width='18%'>"+n.getKg_educationnumber() + "</td><td width='18%'>"+n.getKg_jointime()+"</td><td width='15%'>"+n.getKg_sex()+"</td><td width='18%'>"+n.getKg_parentName()+"</td><td width='15%'>"+n.getKg_parentPhoneNumber()+"</td></tr>";
                tbody+=trs;
            });
            $("#datadiv table").html(tbody);

            var pageCount = data.pageCount; //取到pageCount的值
            var currentPage = data.CurrentPage; //得到currentPage

            var options = {
                bootstrapMajorVersion: 3, //版本
                currentPage: currentPage, //当前页数
                totalPages: pageCount, //总页数
                numberOfPages: 10,
                itemTexts: function (type, page, current) {
                    switch (type) {
                        case "first":
                            return "首页";
                        case "prev":
                            return "上一页";
                        case "next":
                            return "下一页";
                        case "last":
                            return "末页";
                        case "page":
                            return page;
                    }
                },//点击事件，用于通过Ajax来刷新整个list列表
                onPageClicked: function (event, originalEvent, type, page) {
                    $.ajax({
                        url: "/student/studentlist",
                        type: "get",
                        dataType : "json",
                        data:{"teacherphone":phone,"classname":classname, "pageNum":pageNum,"pageSize":pageSize},
                        success: function (data) {
                            $.each(data.list, function(i, n) {
                                var trs = "";
                                trs += "<tr><td width='15%'>" + n.getKg_fullname() + "</td><td width='18%'>"+n.getKg_educationnumber() + "</td><td width='18%'>"+n.getKg_jointime()+"</td><td width='15%'>"+n.getKg_sex()+"</td><td width='18%'>"+n.getKg_parentName()+"</td><td width='15%'>"+n.getKg_parentPhoneNumber()+"</td></tr>";
                                tbody+=trs;
                            });
                            $("#datadiv table").html(tbody);
                        }
                    });
                }
            };
            $('#example').bootstrapPaginator(options);
        }
    });
}

$(document).ready(function(){
    initTable();
});