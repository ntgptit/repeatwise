/**
 * useMutation Hook
 * 
 * Generic mutation hook for any HTTP method
 * 
 * ✅ Tên rõ ràng: useMutation - diễn đạt rõ tác dụng
 * ✅ Type-safe: Sử dụng TypeScript Generics cho data/variables/error types
 * ✅ Clean API: Trả về object rõ ràng với tên gợi nghĩa
 */

import {
  useMutation as useMutationHook,
  useQueryClient,
  type UseMutationOptions,
} from '@tanstack/react-query'

export interface UseMutationReturn<TData, TVariables, TError> {
  mutate: (variables: TVariables) => void
  mutateAsync: (variables: TVariables) => Promise<TData>
  isPending: boolean
  isError: boolean
  error: TError | null
  isSuccess: boolean
  data: TData | undefined
  reset: () => void
}

/**
 * useMutation - Generic mutation hook
 * 
 * @template TData Response data type (required, no default unknown)
 * @template TVariables Request variables type (required, no default unknown)
 * @template TError Error type (defaults to Error)
 * @param mutationFn Mutation function
 * @param options Optional React Query mutation options (callbacks for UI layer)
 * @returns Object with mutation methods and states
 */
export function useMutation<
  TData,
  TVariables,
  TError = Error,
>(
  mutationFn: (variables: TVariables) => Promise<TData>,
  options?: Omit<
    UseMutationOptions<TData, TError, TVariables, unknown>,
    'mutationFn'
  > & {
    invalidateQueries?: string[][]
  },
): UseMutationReturn<TData, TVariables, TError> {
  const queryClient = useQueryClient()

  const mutation = useMutationHook({
    mutationFn,
    onSuccess: (data, variables, context) => {
      if (options?.invalidateQueries) {
        options.invalidateQueries.forEach((key) => {
          queryClient.invalidateQueries({ queryKey: key })
        })
      }
      options?.onSuccess?.(data, variables, context)
    },
    ...options,
  })

  return {
    mutate: mutation.mutate,
    mutateAsync: mutation.mutateAsync,
    isPending: mutation.isPending,
    isError: mutation.isError,
    error: mutation.error,
    isSuccess: mutation.isSuccess,
    data: mutation.data,
    reset: mutation.reset,
  }
}
