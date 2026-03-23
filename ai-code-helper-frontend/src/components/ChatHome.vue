<template>
  <div class="chat-page">
    <header class="chat-header">
      <div class="chat-title">AI 编程小助手</div>
      <div class="chat-meta">聊天室 ID：{{ memoryIdDisplay }}</div>
    </header>

    <main ref="chatLogRef" class="chat-log" aria-live="polite">
      <div
        v-for="message in messages"
        :key="message.id"
        :class="['message-row', message.role === 'user' ? 'row-user' : 'row-ai']"
      >
        <div class="avatar">
          {{ message.role === 'user' ? '你' : 'AI' }}
        </div>
        <div :class="['bubble', message.role === 'user' ? 'bubble-user' : 'bubble-ai']">
          <div class="bubble-text">
            {{ message.content }}
          </div>
        </div>
      </div>

      <div v-if="messages.length === 0" class="empty-hint">
        请输入你的问题，系统会根据你的学习或求职方向进行建议。
      </div>
    </main>

    <form class="chat-input-bar" @submit.prevent="handleSend">
      <input
        v-model="inputMessage"
        class="chat-input"
        type="text"
        autocomplete="off"
        placeholder="输入你的问题（例如：Vue 面试怎么答）"
        :disabled="isStreaming"
      />
      <button class="send-button" type="submit" :disabled="isStreaming || !inputMessage.trim()">
        {{ isStreaming ? '生成中...' : '发送' }}
      </button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { streamAiChat } from '../api/ai'

type ChatRole = 'user' | 'ai'

interface ChatMessage {
  id: string
  role: ChatRole
  content: string
}

const memoryId = ref<number>(0)
const inputMessage = ref<string>('')
const messages = ref<ChatMessage[]>([])
const isStreaming = ref<boolean>(false)

const chatLogRef = ref<HTMLDivElement | null>(null)
let abortController: AbortController | null = null

const memoryIdDisplay = computed(() => {
  return memoryId.value === 0 ? '生成中...' : String(memoryId.value)
})

function generateMemoryId(): number {
  // 生成一个稳定的 int 范围内的会话标识，用于后端 memoryId 参数
  const timestampSeconds = Math.floor(Date.now() / 1000)
  const randomSuffix = Math.floor(Math.random() * 100000)
  const combined = timestampSeconds * 100000 + randomSuffix
  // int32 最大值：2147483647
  return combined % 2147483647
}

function createMessageId(): string {
  if (typeof crypto !== 'undefined' && 'randomUUID' in crypto) {
    return crypto.randomUUID()
  }
  return `msg_${Date.now()}_${Math.random().toString(16).slice(2)}`
}

let isScrollScheduled = false
function scheduleScrollToBottom() {
  if (isScrollScheduled) return
  isScrollScheduled = true

  requestAnimationFrame(async () => {
    isScrollScheduled = false
    await nextTick()
    const el = chatLogRef.value
    if (!el) return
    el.scrollTop = el.scrollHeight
  })
}

async function handleSend() {
  const trimmed = inputMessage.value.trim()
  if (!trimmed || isStreaming.value) return

  if (memoryId.value === 0) return

  inputMessage.value = ''

  const userMessage: ChatMessage = {
    id: createMessageId(),
    role: 'user',
    content: trimmed
  }

  const aiMessage: ChatMessage = {
    id: createMessageId(),
    role: 'ai',
    content: ''
  }

  messages.value.push(userMessage, aiMessage)
  await nextTick()
  scheduleScrollToBottom()

  abortController?.abort()
  abortController = new AbortController()
  isStreaming.value = true

  try {
    await streamAiChat({
      memoryId: memoryId.value,
      message: trimmed,
      signal: abortController.signal,
      onChunk: (chunk) => {
        const targetMessage = messages.value.find((message) => message.id === aiMessage.id)
        if (!targetMessage) return
        targetMessage.content += chunk
        scheduleScrollToBottom()
      }
    })
  } catch {
    const targetMessage = messages.value.find((message) => message.id === aiMessage.id)
    if (targetMessage) {
      targetMessage.content += targetMessage.content
        ? '（请求失败，请稍后重试）'
        : '（请求失败，请稍后重试）'
    }
  } finally {
    isStreaming.value = false
  }
}

onMounted(() => {
  memoryId.value = generateMemoryId()
})

onBeforeUnmount(() => {
  abortController?.abort()
})
</script>

<style scoped>
.chat-page{
  height: 100%;
  display: flex;
  flex-direction: column;
}

.chat-header{
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  background: #ffffff;
  border-bottom: 1px solid rgba(0,0,0,0.06);
}

.chat-title{
  font-weight: 700;
  color: #111827;
}

.chat-meta{
  color: #6b7280;
  font-size: 13px;
}

.chat-log{
  flex: 1;
  overflow: auto;
  padding: 18px 16px;
}

.message-row{
  display: flex;
  gap: 10px;
  margin-bottom: 12px;
  align-items: flex-end;
}

.row-user{
  justify-content: flex-end;
}

.row-user .avatar{
  order: 2;
}

.row-user .bubble{
  order: 1;
}

.avatar{
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: rgba(17, 24, 39, 0.06);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #374151;
}

.bubble{
  max-width: min(720px, 80%);
  border-radius: 14px;
  padding: 10px 12px;
}

.bubble-user{
  background: #4f46e5;
  color: #ffffff;
}

.bubble-ai{
  background: #ffffff;
  color: #111827;
  border: 1px solid rgba(17, 24, 39, 0.08);
}

.bubble-text{
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.5;
}

.empty-hint{
  margin-top: 40px;
  color: #6b7280;
  text-align: center;
}

.chat-input-bar{
  display: flex;
  gap: 10px;
  padding: 12px 16px;
  background: #ffffff;
  border-top: 1px solid rgba(0,0,0,0.06);
}

.chat-input{
  flex: 1;
  border: 1px solid rgba(0,0,0,0.12);
  border-radius: 10px;
  padding: 10px 12px;
  outline: none;
  font-size: 14px;
}

.chat-input:disabled{
  background: #f3f4f6;
  cursor: not-allowed;
}

.send-button{
  min-width: 110px;
  border: none;
  border-radius: 10px;
  padding: 10px 14px;
  background: #4f46e5;
  color: #ffffff;
  font-size: 14px;
  cursor: pointer;
}

.send-button:disabled{
  background: rgba(79, 70, 229, 0.5);
  cursor: not-allowed;
}
</style>

