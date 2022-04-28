<div align="center">
   <img src="https://github.com/PortableOJ/portable-docs/blob/master/img/favicon.png?raw=true" alt="Portable OJ"/>
   <br>
   <h1>Portable Online Judge</h1>
   <p>一个高性能、分布式、易部署的开源 Online Judge 系统</p>
<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
<a href="https://github.com/PortableOJ/portable-server/blob/master/README.md"><img src="https://img.shields.io/badge/all_contributors-1-orange.svg" alt="Contributors"></a>
<!-- ALL-CONTRIBUTORS-BADGE:END -->
  <a href="/LICENSE"><img src="https://img.shields.io/badge/license-GPL%203.0-blue.svg" alt="GPL 3.0 LICENSE"></a>
  <a href="https://github.com/PortableOJ/portable-server/actions/"><img src="https://github.com/PortableOJ/portable-server/actions/workflows/maven-docker.yml/badge.svg" alt="Build status"></a>
</div>

# 本系统的特色功能与 TODO 项目

- [x] 题面对 markdown 支持，支持 latex 数学公式语法
- [x] 界面主打优美和简洁，完全创新的 UI 库，更多的动画效果但是更少的代码量，对网络带宽压力小
- [x] 题库严格管理，自动生成输出文件，提供自动化的题目约束检查
- [x] 比赛支持多人出题，合作完成比赛
- [x] 比赛支持出题人专属测试能力，避免误打误撞提交到比赛中，同时提供全时段的测试能力
- [x] 支持批量用户，并提供了 IP 锁定能力
- [x] Judge 支持分布式部署，快速动态扩容以及实时监测
- [ ] 对两次提交间隔进行限制
- [ ] 增加主动限制所有低于某一用户组的用户登录
- [ ] 博客系统

# 部署 & 使用帮助

请前往 [官方文档](https://portableoj.github.io/portable-docs) 以获取更加详细的帮助

或者前往 [Discussions](https://github.com/PortableOJ/portable-server/discussions) 来寻求帮助

# 项目相关仓库链接

- [前端](https://github.com/PortableOJ/portable-web)
- [后端](https://github.com/PortableOJ/portable-server)
- [判题系统](https://github.com/PortableOJ/portable-judge)
- [环境支持](https://github.com/PortableOJ/portable-judge-base)
- [UI 库](https://github.com/PortableOJ/mevcl)
- [部署工具](https://github.com/PortableOJ/portable-deploy)
- [文档](https://github.com/PortableOJ/portable-docs)

# 贡献本项目

## 我发现了系统错误

请根据下列操作进行

- 前往 [Issues](https://github.com/PortableOJ/portable-server/issues)
- 查询是否已经有一个 open 状态的 Issue，且和我发现的相关的系统错误内容？若有，则不需要做任何事情
- 查询是否已经有一个 close 状态的 Issue，且和我发现的相关的系统错误内容
  - 请阅读其中的内容
  - 是否已经对这个问题给出了最终的答复？若是，则不需要做任何事情
- 请创建一个新的 Issues 并根据要求描述你的问题

## 我希望能增加新功能，或者讨论与分享内容

- 前往 [Discussions](https://github.com/PortableOJ/portable-server/discussions) 并根据「讨论区指南」中的说明创建新的讨论

## 我希望由我来更新代码

请详细阅读 [CONTRIBUTING](./CONTRIBUTING.md) 的内容，并根据规范来提交你的代码。如果不依照规范提交，你的代码很有可能被拒绝

# LICENSE

本项目的所有代码均使用基于 GNU General Public License v3.0 开源协议开源，并附加下列条款。部分衍生项目采用 MIT License 开源，请以目标仓库内的 LICENSE 文件与 README 说明为准

请依照开源协议规范进行二次开发和使用

### 注明项目源以及所有开发者

此软件及其所有副本的源码、二次开发的软件及其所有副本的源码，上述所有源码内必须在其根目录的明显位置，注明其来源的源码地址、最初源码地址（即本组织下的仓库）、来源的源码的所有贡献者。

同时，在所有上述的源码的产生的发行版本、部署版本的程序的页面的明显位置，需要注明此发行版本的源码地址、最初源码地址（即本组织下的仓库）、发行版本的所有贡献者所有贡献者。

例如，若本项目的开发者为 A，而 B 通过复制本项目得到新的项目，并对其进行了修改，则 B 需要在新的项目下注明本项目的最初地址（即本项目）以及开发者 A 和 B。同时有 C 通过复制 B 的项目得到一个新的项目，C
没有修改其任何源码，而是使用此项目的源码进行商业活动，则 C 需要在新的项目下注明 B 的项目地址、本项目的最初地址、开发者 A 和 B，同时其商业活动的主页面上，应当注明 C 的项目地址、本项目的最初地址、开发者 A 和 B

## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="http://hukeqing.github.io"><img src="https://avatars.githubusercontent.com/u/47495915?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Mauve</b></sub></a><br /><a href="https://github.com/PortableOJ/portable-server/commits?author=Hukeqing" title="Tests">⚠️</a> <a href="https://github.com/PortableOJ/portable-server/commits?author=Hukeqing" title="Code">💻</a> <a href="#ideas-Hukeqing" title="Ideas, Planning, & Feedback">🤔</a> <a href="#maintenance-Hukeqing" title="Maintenance">🚧</a> <a href="https://github.com/PortableOJ/portable-server/pulls?q=is%3Apr+reviewed-by%3AHukeqing" title="Reviewed Pull Requests">👀</a></td>
  </tr>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!
