## Overview ##

Synopsys Rapid Scan Static for Jenkins simplifies running Synopsys Rapid Scan Static commands in Jenkins builds.

## Build ##

# Where can I get the latest release?

You can download the latest artifact from artifactory. The latest version is always the most up to date<br />
SNAPSHOT versions can be found at [synopsys-sigma-jenkins-local/unreleased/](http://artifactory.internal.synopsys.com:80/artifactory/synopsys-sigma-jenkins-local/unreleased)<br />
RELEASED versions can be found at [synopsys-sigma-jenkins-local/released/](http://artifactory.internal.synopsys.com:80/artifactory/synopsys-sigma-jenkins-local/released)

## Documentation ##

All documentation for Synopsys Rapid Scan Static for Jenkins can be found on [confluence](https://sig-confluence.internal.synopsys.com/display/SIGMA/Jenkins+Plugin)

## Release Steps ##
<ol>
<li>Cut a release branch for the version that we want to release</li>
<li>In the build.gradle file of the new release branch, remove the "-SNAPSHOT" from the version number and commit</li>
<li>Tag the release branch as the version of the release</li>
<li>The build pipeline will kick off a build and create the release in Artifactory</li>
<li>Checkout the main branch (master).</li>
<li>Edit the build.gradle file to the next snapshot version. </li>
