const file_input = document.getElementById("file-input");

const name_input = document.getElementById("name-input");
const token_input = document.getElementById("token-input");
const page_id_input = document.getElementById("page-id-input");

function login() {
    return fetch("http://localhost:4567/api/v1/login", {
        method: 'POST',
        credentials: 'include'
    }).catch(error => console.log(error))
    .then(response => {
        if (!response.ok)
            throw new Error("HTTP error " + response.status);
        return response;
    });
}

const uploadButton = document.getElementById("upload-button");
uploadButton.addEventListener("click", function(evt) {
    // Get the file from the file input
    let file = file_input.files[0];    
    let upload_url = `http://localhost:4567/api/v1/upload?name=${file.name}`;

    // first, login
    login().then(response => {
        // now, upload file
        fetch(upload_url, {
            method: 'POST',
            credentials: 'include',
            body: file,
        }).then(response => {
            if (!response.ok)
                throw new Error("HTTP error " + response.status);
            return response.json();
        })
        .then(json => console.log(json))
        .catch(error => console.log(error));
    });
});

const exportButton = document.getElementById("export-button");
exportButton.addEventListener("click", function(evt) {
    let name = name_input.value;
    let token = token_input.value;
    let page_id = page_id_input.value;
    
    let export_url = "http://localhost:4567/api/v1/export";
    let options = {
        "name": name,
        "type": "notion",
        "token": token,
        "pageId": page_id
    };

    login().then(response => {
        fetch(export_url, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(options)
        }).then(response => {
            if (!response.ok)
                throw new Error("HTTP error " + response.status);
            return response.json();
        })
        .then(json => console.log(json))
        .catch(error => console.log(error));
    });
});
