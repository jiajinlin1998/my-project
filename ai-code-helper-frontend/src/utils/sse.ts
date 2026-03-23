export function parseSseDataFromBuffer(buffer: string): {
  dataList: string[]
  remainingBuffer: string
} {
  // 统一换行，避免不同平台的 \r\n 影响分隔
  let workingBuffer = buffer.replace(/\r\n/g, '\n')
  const dataList: string[] = []

  while (true) {
    const delimiterIndex = workingBuffer.indexOf('\n\n')
    if (delimiterIndex === -1) break

    const eventBlock = workingBuffer.slice(0, delimiterIndex)
    workingBuffer = workingBuffer.slice(delimiterIndex + 2)

    // 忽略空事件块
    if (eventBlock.trim() === '') continue

    const lines = eventBlock.split('\n')
    const dataLines: string[] = []

    for (const line of lines) {
      // SSE 事件中只有 data 行会被后端用于文本输出
      if (line.startsWith('data:')) {
        dataLines.push(line.slice(5).trimStart())
      }
    }

    if (dataLines.length > 0) {
      // 同一事件可能分多行 data，这里按 SSE 语义用换行拼接
      dataList.push(dataLines.join('\n'))
    }
  }

  return { dataList, remainingBuffer: workingBuffer }
}

