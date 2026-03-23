import { parseSseDataFromBuffer } from '../utils/sse'

export interface StreamAiChatOptions {
  memoryId: number
  message: string
  signal: AbortSignal
  onChunk: (chunk: string) => void
}

export async function streamAiChat(options: StreamAiChatOptions): Promise<void> {
  const { memoryId, message, signal, onChunk } = options

  const queryParams = new URLSearchParams({
    memoryId: String(memoryId),
    message
  })

  const response = await fetch(`/api/ai/chat?${queryParams.toString()}`, {
    method: 'GET',
    headers: {
      // 告诉服务端我们希望返回 SSE 格式
      Accept: 'text/event-stream',
      'Cache-Control': 'no-cache'
    },
    signal
  })

  if (!response.ok) {
    throw new Error(`请求失败，状态码：${response.status}`)
  }

  if (!response.body) return

  const textDecoder = new TextDecoder('utf-8')
  const responseReader = response.body.getReader()

  let buffer = ''

  while (true) {
    const { value: bytes, done } = await responseReader.read()
    if (done) break
    if (!bytes) continue

    buffer += textDecoder.decode(bytes, { stream: true })

    const { dataList, remainingBuffer } = parseSseDataFromBuffer(buffer)
    buffer = remainingBuffer

    for (const chunk of dataList) {
      onChunk(chunk)
    }
  }

  // 将最后一次解码残留的数据刷新出来
  buffer += textDecoder.decode()
  const { dataList } = parseSseDataFromBuffer(buffer)
  for (const chunk of dataList) {
    onChunk(chunk)
  }
}

