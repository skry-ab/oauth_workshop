# OAuth workshop

In this workshop you will setup authentication using `OpenID Connect` (OIDC)
for a simple shoutbox webpage. The frontend is created using `React` with Typescript and
the backend is using `Ktor` (with kotlin).

* The walkthrough assumes that Google is used as the OIDC provider, but any provider
  will work. The frontend and backend has all the dependencies installed that you will
  need.
* You can either use the API-keys provided with the walkthrough, or create a new project in the Google Cloud console for OIDC.
* Sample solutions to the steps of the workshop can be found in this repository.

## Step 0

* [ ] Start the backend and frontend, and test that the application is working.

### DoD:
- [ ] You should be able to post new messages, as well as see posted messages.

## Step 1

* Setup the JWT plugin for the backend, and add JWT authorization for the POST request.
* The JWT plugin should verify that the JWT contains an `email` claim.
* The Ktor documentation has a good example on how this can be done: [https://ktor.io/docs/jwt.html](https://ktor.io/docs/jwt.html)
    * Note: The Google OIDC provider is using `RS256` for verification of tokens.
    * [https://developers.google.com/identity/protocols/oauth2/openid-connect#validatinganidtoken](https://developers.google.com/identity/protocols/oauth2/openid-connect#validatinganidtoken)
* The author field should be set based on the email set in the ID token.
    * The email claim can be extracted from the JWT using the following snippet:
```kotlin
// In the authenticated get-endpoint
val principal = call.principal<JWTPrincipal>() ?: error("No principal")
val email = principal.payload.getClaim("email").asString()
```

### DoD:

* You should see that POST requests to the messages endpoint result in `401 Unauthorized`.

## Step 2

* Show a login button instead of the submit message form if no token exists, that redirects the user to the `/login` endpoint of the backend.

### DoD:
- [ ] When visiting the page, the login button should be shown instead of the submit message form.
- [ ] When pressing the login button, the user should be redirected to the `/login` endpoint of the backend.

## Step 3
* Setup the Ktor OAuth plugin and configure it to connect to the Google OIDC endpoint.
    * The user email should be requested as a claim. See [https://developers.google.com/identity/protocols/oauth2/scopes#oauth2](https://developers.google.com/identity/protocols/oauth2/scopes#oauth2) for a reference of possible claims.
* Create both the configuration, as well as the `/login` and `/callback` endpoints. You can leave the endpoints empty.
    * Refer to the Ktor documentation if you are unsure: [https://ktor.io/docs/oauth.html](https://ktor.io/docs/oauth.html)

### DoD:
- [ ] When a user visits the `/login` endpoint, they should be redirected to a Google sign-in page.
- [ ] When a user logs in at the Google sign-in page, the user should be redirected back to the backend.
- [ ] The authorization token should be visible as a query-parameter in the browsers address field.

## Step 4
* Implement the callback endpoint, redirect to frontend with id-token as a query parameter.
    * The ID-token can be extracted in the callback endpoint with the following snippet:
```kotlin
get("/callback") {
    val principal: OAuthAccessTokenResponse.OAuth2 = call.principal() ?: error("No principal returned")

    val idTokenString = principal.extraParameters["id_token"] ?: error("No ID-token returned")
}
```

### DoD:
- [ ] When the user performs a login, the user should be redirected back to the frontend with the ID-token in a `token` query-parameter.
    * Like so: `http://localhost:3000/?token=ID_TOKEN`
- [ ] The ID-token should contain an `email` claim. You can check this by decoding the JWT in your code, or by entering the JWT here: [https://www.jstoolset.com/jwt](https://www.jstoolset.com/jwt).

## Step 5
* Create a receiving `/callback` endpoint for the token in the frontend.
* The endpoint should extract the token from the query-parameter and store it.
* After storing the token, the user should be redirected back to the main page (`/`).

### DoD:
- [ ] When performing the login flow, the user should be redirected back to the main page and the post-message form should be visible.

## Step 6
* Attach the token with the post-message request.
* The JWT-Auth plugin in Ktor expects the token to be provided in the `Authorization` header like so:
    * `Authorization: Bearer TOKEN`

### DoD:
- [ ] The user should be able to post messages after having logged in.
