<template>
  <router-view />
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: #f5f7fa;
}

/* Element Plus 数字输入默认居中，统一改为左对齐 */
.el-input-number .el-input__inner {
  text-align: left !important;
}
</style>
