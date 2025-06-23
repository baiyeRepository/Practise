
<template>
<div class="card">
  <comment :fid="0" module="message"></comment>
</div>
</template>
<script>
import Comment from '@/components/Comment'
export default {
  name: "UserComment",
  components: {Comment},
}
</script>

<style scoped>

</style>