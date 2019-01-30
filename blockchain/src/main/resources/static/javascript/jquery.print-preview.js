/*!
 * jQuery Print Previw Plugin v1.0.1
 *
 * Copyright 2011, Tim Connell
 * Licensed under the GPL Version 2 license
 * http://www.gnu.org/licenses/gpl-2.0.html
 *
 * Date: Wed Jan 25 00:00:00 2012 -000
 */
 
(function($) {
    var iframeHtml = "";
	$.fn.printPreview = function() {
		this.each(function() {
			$(this).on('click', function(e) {
			    e.preventDefault();
                $('#print-modal').html('');
			    //if (!$('#print-modal').length) {
			        $.printPreview.loadPrintPreview();
			    //}
			});
		});
		return this;
	};
    
    // Private functions
    var mask, size, print_modal, print_controls;
    $.printPreview = {
        loadPrintPreview: function() {
            print_modal = $('<div id="print-modal"></div>');
            print_controls = $('<div id="print-modal-controls">' +
                '<a href="#" class="close" title="Close">关闭</a>').hide();
            $.printPreview.GetIframeHtml();
            var print_frame = $(iframeHtml);
            print_modal
                .hide()
                .append(print_controls)
                .append(print_frame)
                .appendTo('body');

            var print_frame_ref = $("#print-modal-content").document;

            $('a', print_frame_ref).bind('click.printPreview', function(e) {
                e.preventDefault();
            });
            
            $('head').append('<style type="text/css">' +
                '@media print {' +
                    '/* -- Print Preview --*/' +
                    '#print-modal-mask,' +
                    '#print-modal {' +
                        'page-break-after: always;' +
                    '}' +
                    '.pagewrap {' +
                        'display: none !important;' +
                    '}' +
                '}' +
                '</style>'
            );

            if (iframeHtml == ""){
                return;
            }
            iframeHtml = "";
            $.printPreview.loadMask();
            $('body').css({overflowY: 'hidden', height: '100%'});
            starting_position = $(window).height() + $(window).scrollTop();
            var css = {
                    top:         starting_position,
                    height:      '100%',
                    overflowY:   'auto',
                    zIndex:      10000,
                    display:     'block'
                }
            print_modal
                .css(css)
                .animate({ top: $(window).scrollTop()}, 400, 'linear', function() {
                    print_controls.fadeIn('slow').focus();
                });

            // Bind closure
            $('a', print_controls).bind('click', function(e) {
                e.preventDefault();
                if ($(this).hasClass('print')) { window.print(); }
                else {
                    $.printPreview.distroyPrintPreview();
                }
            });
    	},
    	
    	distroyPrintPreview: function() {
    	    print_controls.fadeOut(100);
    	    print_modal.animate({ top: $(window).scrollTop() - $(window).height(), opacity: 1}, 400, 'linear', function(){
    	        print_modal.remove();
    	        $('body').css({overflowY: 'auto', height: 'auto'});
    	    });
    	    mask.fadeOut('slow', function()  {
    			mask.remove();
    		});				

    		mask.unbind("click.printPreview.mask");
    		$(window).unbind("resize.printPreview.mask");
	    },
	    
    	/* -- Mask Functions --*/
	    loadMask: function() {
	        size = $.printPreview.sizeUpMask();
            mask = $('<div id="print-modal-mask" />').appendTo($('body'));
    	    mask.css({				
    			position:           'absolute', 
    			top:                0, 
    			left:               0,
    			width:              size[0],
    			height:             size[1],
    			display:            'none',
    			opacity:            0,					 		
    			zIndex:             9999,
    			backgroundColor:    '#000'
    		});
	
    		mask.css({display: 'block'}).fadeTo('400', 0.75);
    		
            $(window).bind("resize..printPreview.mask", function() {
				$.printPreview.updateMaskSize();
			});
			
			mask.bind("click.printPreview.mask", function(e)  {
				$.printPreview.distroyPrintPreview();
			});
        },
    
        sizeUpMask: function() {
            if ($.browser.msie) {
            	var d = $(document).height(), w = $(window).height();
            	return [
            		window.innerWidth || 						// ie7+
            		document.documentElement.clientWidth || 	// ie6  
            		document.body.clientWidth, 					// ie6 quirks mode
            		d - w < 20 ? w : d
            	];
            } else { return [$(document).width(), $(document).height()]; }
        },
    
        updateMaskSize: function() {
    		var size = $.printPreview.sizeUpMask();
    		mask.css({width: size[0], height: size[1]});
        },

        GetIframeHtml: function (){
            var classname = $("#classSelect").val();
            var year = $("#yearSelect").val();
            var teacherid = $("#teacherid").val();
            var datas = {
                'className' : classname,
                'year': year,
                'teacherId': teacherid
            };
            $.ajax({
                type:"post",
                url:"../electronicscertificate/certificatelist",
                data: JSON.stringify(datas),
                contentType : 'application/json',
                dataType : 'json',
                async: false,
                success:function (data) {
                    if (data != null && data.length > 0){
                        iframeHtml += '<iframe id="print-modal-content" style="width:96%; height:780px;" frameborder="0" name="print-frame" src="../electronicscertificate/certificate/showPdf" />';
                    }else{
                        M.dialog13 = jqueryAlert({
                            'icon': '../images/alertimgs/warning.png',
                            'content': '暂无可以打印的证书信息',
                            'closeTime': 2000,
                        })
                        M.dialog13.show();
                    }
                },error: function () {
                    M.dialog13 = jqueryAlert({
                        'icon': '../images/alertimgs/warning.png',
                        'content': '请求失败',
                        'closeTime': 2000,
                    })
                    M.dialog13.show();
                }
            });
            //}
        }
    }
})(jQuery);