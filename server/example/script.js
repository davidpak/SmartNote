file_input = document.getElementById("file-input");

button = document.getElementById("upload-button");
button.addEventListener("click", function(evt) {
    // Get the file from the file input
    let file = file_input.files[0];

    let login_url = "http://localhost:4567/api/v1/login";
    let upload_url = `http://localhost:4567/api/v1/upload?name=${file.name}`;

    // first, login
    fetch(login_url, {
        method: 'POST'
    }).then(response => {
        if (!response.ok)
            throw new Error("HTTP error " + response.status);

        // get auth token
        let auth = response.headers.get("Authorization");

        // now, upload file
        fetch(upload_url, {
            method: 'POST',
            headers: {
                'Authorization': auth
            },
            body: file
        }).then(response => {
            if (!response.ok)
                throw new Error("HTTP error " + response.status);
            return response.json();
        })
        .then(json => console.log(json))
        .catch(error => console.log(error));
    }).catch(error => console.log(error));
});
