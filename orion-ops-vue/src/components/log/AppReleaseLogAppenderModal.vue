<template>
  <a-modal v-model="visible"
           :closable="false"
           :title="null"
           :footer="null"
           :dialogStyle="{top: '16px', padding: 0}"
           :bodyStyle="{padding: 0}"
           :destroyOnClose="true"
           :forceRender="true"
           @cancel="close"
           width="96%">
    <!-- 日志面板 -->
    <AppReleaseLogAppender ref="logger" appenderHeight="calc(100vh - 153px)" height="calc(100vh - 34px)"/>
  </a-modal>
</template>

<script>
import AppReleaseLogAppender from '@/components/log/AppReleaseLogAppender'

export default {
  name: 'AppReleaseLogAppenderModal',
  components: {
    AppReleaseLogAppender
  },
  data() {
    return {
      visible: false
    }
  },
  methods: {
    open(id) {
      this.visible = true
      setTimeout(() => {
        this.$refs.logger.open(id)
      }, 300)
    },
    close() {
      this.visible = false
      this.$nextTick(() => {
        this.$refs.logger.close()
      })
    }
  }
}
</script>

<style lang="less" scoped>
</style>
