(function($) {

    $(function() {
        $('.sidebar-widget').on('click', 'h3', function(){
            $(this).closest('.sidebar-widget').find('ul').toggleClass('show');
        });
    });

}(jQuery));

// Toggle Category sidebar widget
(function($) {

    $(function() {
        $('.widget-section, .widget_categories, .widget_nav_menu, .widget_text, .widget_archive, .widget_recent_entries').on('click', '.js-widget-toggle', function(){
            $(this).toggleClass('js-widget-toggle--active').parent().find('.sidebar-nav, .widget-nav, .textwidget').toggleClass('show');
        });
    });

    $(function() {

        $( ".widget_categories li, .widget-nav li" ).has( "ul" ).addClass('dropdown').each(function(){
            $('>a', this).removeAttr("href").css( 'cursor', 'pointer' );
        });

        $( ".widget_categories li.dropdown, .widget-nav li.dropdown").on('click', function(e){
            $(this)
                .toggleClass('open')
                .find('.children, .sub-menu').slideToggle();
        });

    });

}(jQuery));
