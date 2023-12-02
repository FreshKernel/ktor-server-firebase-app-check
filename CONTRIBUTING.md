# Contributing

The contributions are more than welcome! <br>
This project will be better with the open-source community help

There are no guidelines for now.
This page will be updated in the future.

## Steps to contributing

You will need GitHub account as well as git installed and configured with your GitHub account on your machine

1. Fork the repository in GitHub
2. clone the forked repository using `git`
3. Add the `upstream` repository using:
    ```
    git remote add upstream git@github.com:freshplatform/ktor-server-firebase-app-check.git
    ```
4. Open the project with your favorite IDE, we suggest using [IntelliJ IDEA Community Edition](https://www.jetbrains.com/idea/download/)
5. Sync the project with Gradle
6. Create a new git branch and switch to it using:
    ```
    git checkout -b your-branch-name
    ```
    The `your-branch-name` is your choice
7. Make your changes
8. Test them in the [example](./example) and add changes in necessary
9. Mention the new changes in the [CHANGELOG.md](./CHANGELOG.md) in the next block
10. When you are done to send your pull request, run:
    ```
    git add .
    git commit -m "Your commit message"
    git push origin your-branch-name
    ```
    this will push the new branch to your forked repository
11. Now you can send your pull request either by following the link that you will get in the command line or open your
forked repository. You will find an option to send the pull request, you can also
open the [Pull Requests](https://github.com/freshplatform/ktor-server-firebase-app-check/pulls) tab and send new pull request
12. Please wait for the review, and we might ask you to make more changes, then run:
```
git add .
git commit -m "Your new commit message"
git push origin your-branch-name
```