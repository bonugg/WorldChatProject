if( localStorage.getItem("Authorization") == null){
    location.href = "/login_page";
}else {
    $.ajax({
        type: 'POST',
        url: '/api/v1/accessToken',
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", localStorage.getItem("Authorization"));
        },
    }).done(function(data,status,xhr) {
        if(data != ""){
            $("#profile").html("hi");
        }else {
            $.ajax({
                type: 'POST',
                url: '/api/v1/refreshToken',
                data : {
                    userName : localStorage.getItem("userName")
                }
            }).done(function(data,status,xhr) {
                let jwtToken = xhr.getResponseHeader('Authorization');
                localStorage.setItem("Authorization", jwtToken);
            }).fail(function(xhr, status, error){
                $.ajax({
                    type: 'POST',
                    url: '/api/v1/logout',
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader("Authorization", localStorage.getItem("Authorization"));
                    },
                }).done(function(data,status,xhr){
                    localStorage.removeItem("Authorization");
                    localStorage.removeItem("userName");
                    location.href = "/login_page";
                }).fail(function (error) {
                    alert("로그아웃");
                    location.href = "/login_page";
                });
            });
        }
    })
}