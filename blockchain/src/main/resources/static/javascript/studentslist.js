var pageNum = 1;
var pageSize = 10;
var classnames;
var phone;
$(document).ready(function(){
    classnames = $("#selectDiv").val();
    phone = $("#phone").val();
    // $(".overlay").css({'display':'block','opacity':'0.8'});
    // $(".showbox").animate({'margin-top':'300px','opacity':'1'});
    initTable(classnames);
});

//ajax获取后台数据
function initTable(classname) {
    var tbody="";
    // $(".overlay").css({'display':'block','opacity':'0.8'});
    // $(".showbox").animate({'margin-top':'300px','opacity':'1'});
    $.ajax({
        type: 'get',
        dataType : "json",
        async: false,
        url: "/student/studentlist",//请求的action路径页面
        data:{"teacherphone":phone,"classname":classname, "pageNum":pageNum,"pageSize":pageSize},
        error: function () {//请求失败处理函数
            alert('请求失败');
        },
        success:function(data){ //请求成功后处理函数。
            if (data.items.length > 0){
                for (var i = 0;i <data.items.length;i++){
                    var trs = "";
                    trs += "<tr data-id='"+ data.items[i].kg_studentprofileid +"' onclick='GoStudentInfo(this)'><td width='15%'>" + data.items[i].kg_fullname + "</td><td width='18%'>"+data.items[i].kg_educationnumber + "</td><td width='18%'>"+data.items[i].kg_jointime+"</td><td width='15%'>"+data.items[i].kg_sex+"</td><td width='18%'>"+data.items[i].kg_parentName+"</td><td width='15%'>"+data.items[i].kg_parentPhoneNumber+"</td></tr>";
                    tbody+=trs;
                }
            }

            $("#datadiv table tbody").html(tbody);
            // $(".showbox").stop(true);
            // $(".overlay").css({'display':'none','opacity':'0'});

            if (data.totalPage > 1){
                var pageCount = data.totalPage; //取到pageCount的值
                var currentPage = data.currentPage; //得到currentPage
                var options = {
                    bootstrapMajorVersion: 2, //版本
                    currentPage: currentPage, //当前页数
                    totalPages: pageCount, //总页数
                    numberOfPages: pageCount,
                    shouldShowPage:true,
                    itemTexts: function (type, page, currentPage) {
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
                        $("#datadiv table tbody").html("");
                        $(".overlay").css({'display':'block','opacity':'0.8'});
                        $(".showbox").animate({'margin-top':'300px','opacity':'1'});
                        var tbodys="";
                        $.ajax({
                            url: "/student/studentlist",
                            type: "get",
                            dataType : "json",
                            data:{"teacherphone":phone,"classname":classname, "pageNum":page,"pageSize":pageSize},
                            success: function (data) {
                                if (data.items.length > 0){
                                    for (var i = 0;i <data.items.length;i++){
                                        var trs = "";
                                        trs += "<tr data-id='"+ data.items[i].kg_studentprofileid +"' onclick='GoStudentInfo(this)'><td width='15%'>" + data.items[i].kg_fullname + "</td><td width='18%'>"+data.items[i].kg_educationnumber + "</td><td width='18%'>"+data.items[i].kg_jointime+"</td><td width='15%'>"+data.items[i].kg_sex+"</td><td width='18%'>"+data.items[i].kg_parentName+"</td><td width='15%'>"+data.items[i].kg_parentPhoneNumber+"</td></tr>";
                                        tbodys+=trs;
                                    }
                                }
                                $(".showbox").stop(true);
                                $(".overlay").css({'display':'none','opacity':'0'});
                                $("#datadiv table tbody").html(tbodys);
                            }
                        });
                    }
                };
                $('#pages').bootstrapPaginator(options);
                // $("#pages ul li").each(function(){
                //     if (this.hasClass("active")){
                //         this.attr("disabled",true);
                //     }
                // });
            }else{
                $("#pages").html('');
            }
        }
    });
}

function GoStudentInfo(obj) {
    var id = $(obj).attr("data-id");
    window.location.href = "/student/studentinfo?id=" + id + "&backpage=teacher";
}
