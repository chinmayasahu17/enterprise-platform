# Enterprise Processing Platform

## Build and publish multi-architecture images

The custom service images are published as multi-architecture manifests for
`linux/amd64` and `linux/arm64`. This lets the same image tag run on Apple
Silicon development machines, Intel Macs, and x86_64 Linux or EC2 k3d nodes.

Log in to the Docker Hub account configured by the Helm chart (`chinmaya1`):

```bash
docker login
```

Enable Docker Buildx and create the builder used by this project (the build
script also performs these steps if the builder does not already exist):

```bash
docker buildx create --name enterprise-platform-multiarch --driver docker-container --use
docker buildx inspect --bootstrap enterprise-platform-multiarch
```

From the repository root, build and push every custom service:

```bash
./build-all.sh
```

Verify that a published image includes both architectures:

```bash
docker buildx imagetools inspect chinmaya1/enterprise-platform-gateway:1.0.1
```

The output should list `linux/amd64` and `linux/arm64` platform manifests.

Deploy the updated images with Helm:

```bash
helm upgrade --install enterprise-platform ./platform-parent
```
