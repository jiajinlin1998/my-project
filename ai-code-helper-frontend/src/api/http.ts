import axios from 'axios'

export const httpClient = axios.create({
  baseURL: '/api',
  // SSE 依赖服务端按需持续输出，这里不设置超时避免过早中断
  timeout: 0
})

